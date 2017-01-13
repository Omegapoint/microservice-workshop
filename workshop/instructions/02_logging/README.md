Logging
-------
In an microservice architecture, which could have several hundreds of services, it is no longer feasible to ssh in to specific machines and looking at logs.
We also need a way to figure out the request path taken in case something goes wrong, to figure out where the problem occurred.

In this workshop, we are going to use the **ELK** (**E**lasticsearch**L**ogstash**K**ibana) stack to perform distributed logging. 
If you want to know more about ELK before or after you start, read the appendix at the bottom.

# Setup
The teacher has already setup an ELK cluster, so you dont need to do that :-). The logstash instance is configured to receive JSON formed logs.

# Steps

## Writing logs
1. Add some logging to your application. We should use a proper logger. An example would be to continue with the HelloWorldController.
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//...

private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);

//...

LOGGER.info("Someone wanted to say hello!");
```
Restart the application and visit the endpoint. Verify that the logging is outputted into your console.

## Sending logs to ELK
We now need to send logs to the ELK cluster. There are multiple ways of doing this, a common one is to use a program called `Filebeat`. Filebeat inspects selected
log files. If any change as occurred, it will parse the change and send it to Logstash. Logstash will then parse it and hand it over to Elasticsearch. This can be
extra useful if you cannot control the log writing yourself (e.g. legacy system) but you still want it into ELK.

In our use case, it is much simplier though. We will add an appender that will asynchronously send logs to Logstash directly. 

1. We need to add a new dependency to Logstash Logback Encoder (https://github.com/logstash/logstash-logback-encoder) for the appender. In `pom.xml`, add
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>4.8</version>
</dependency>
```

2. Create a new logback configuration, i.e. `src/resources/logback.xml`. There are multiple appenders to choose from, we will pick the TCP one. Add the content 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="stash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>@master.dns@:5044</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
    </appender>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="stash" />
    </root>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```
Here, we have two appenders, one which outputs to console and one which sends to Logstash via TCP.

3. Restart the application. Open a browser and visit an endpoint which performs logging. Verify that it was printed in the console.

4. Go to Kibana, link is given by your teacher. Go to the `Discover` page to view the incoming logs. Can you find your message? The next step will make it easier

## Improving log information
It is possible to find our logs in ELK, however we are lacking some information. We want to know which application that sent the logs, which endpoint was called and so on.
This can be done by adding filters to the filter chain.

1. We want to add the filters to our configuration. Open `WorkshopApplication` and add the following to the **configuration** (inside). The imports are from javax.servlet, logback and Spring.

Imports
```java
import javax.servlet.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
```

Configuration
```java
@Value("${spring.application.name}")
public String applicationName;

@Bean
// Logback filter which includes request information
public Filter requestLoggingFilter() {
    return new MDCInsertingServletFilter();
}

@Bean
// Custom filter to include application name to all logging event performed on a request.
public Filter customLoggingFilter() {
    return new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {}

        @Override
        public void destroy() {}

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            MDC.put("application", applicationName);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    };
}
```

2. Visit Kibana again. Go to the Discover page. Filter for your application by using
```
application: APPLICATION_NAME
``` 
Can you find your logs?

## Tracing over multiple services
When request fails, we want to where and why it failed. However, in a microservice architecture, the request might have travelled through multiple services where it
failed at one specific service. We require some tracing to see which path the request travelled and when it failed.
 
This can be done by assigning a unique ID for an incoming request. Google Dapper proposes that two ids should be created:
 1. Trace ID. This is passed along in requests to other services.
 2. Span ID. This is only used in the current service.
 
The span id can be used to trace the current service. The trace id can be used to trace the entire path walked for the incoming request throughout all systems.
Of course, Spring has created a library based on this concept - Spring Sleuth.

1. Add a dependency to Spring Sleuth, i.e. in `pom.xml`, add:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

2. The ids (trace, span) has now been added as MDC variables. However, they are not shown in the console. We can add these by updating the logback pattern. In `logback.xml`, update to
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%X{X-B3-SpanId},%X{X-B3-TraceId}] [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
</appender>
```

3. Restart the application. Visit a page which has some logging. View in console and Kibana that the log looks good.

4. Perform some logging in the beginning your hero endpoint and restart (simple 'Finding Heroes' would suffice). 
Open a browser and visit your hero endpoint again. Now open Kibana. 
Can you find the path your request has walked? Search for your logging message. Retrieve the trace id. You can filter on the trace id using
```
X-B3-TraceId: 123456789
```

# Extra
Only these tasks if you are ahead and are only waiting for the next step.

## Tracing an error
If an exception occurs in the request path, it would be nice to find out where. As we have seen before in Kibana, we can see where a request have travelled. 
We shall now see if we can find out where an exception occurs. Each service that the teacher has provided contains errors. 

The endpoint previously used, `/superhero` can also accept an input path parameter. It has been reported that "groot" and "loki" are causing issues. 
It is also unknown what would happen if the hero does not exist in the database.
 
Can you figure out which service has problem with which hero? All request should still first go to the SuperHero service, you can modify your request as
```java
ResponseEntity<SuperHeroResponse> result = restTemplate.getForEntity("http://superhero-service/superhero/" + "spiderman", SuperHeroResponse.class);
```

**HINT** It might be useful to select some fields to look at in Kibana instead of everything. `application`, `message` and `X-B3-TraceId` might be suitable.

## Global exception handling
The logging looks fine and all, but that is only when we are manually logging. What about when the system crashes? When will the logs be sent then?
We can add this type of logging by creating an global exception handler. In Spring Boot, a simple way of doing this is creating a `ControllerAdvice`.

1. Create a new class called `GlobalExceptionHandler`. Add the following content:
```java
public class GlobalExceptionHandler {
    // TODO: Add code!
}
```

2. In your application, throw an RuntimeException somewhere to trigger the global handler. Watch the console or in Kibana to see that the exception has been logged.

# Background ELK
### Elasticsearch information
Based on lucene, Elasticsearch provides fast search operations on an index.

### Logstash
Logstash can perform operations on log files, exporting a common log format.

### Kibana
Kibana is a graphical interface which queries Elasticsearch. It is an easy way to get an overview of the logs.


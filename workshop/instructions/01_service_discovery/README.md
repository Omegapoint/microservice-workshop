Service discovery
-----------------
This part will show how to enable service discovery in your system. The goal of this part is to:
1. Connect and register to Eureka.
2. Retrieve list of available services from Eureka
3. Communicate with a service without knowing the address to it.

# Setup
Boilerplate code can be found in the root folder "workshop". 
The code consists of a clean Spring Boot application.

# Steps

## Verifying setup
1. Open the project in your editor. Let it load.
2. Run main#WorkshopApplication. Wait for it to start, ending with `Started WorkshopApplication in x seconds` 
3. Open a browser and visit `http://localhost:8080/hello`. You should see the response `Hello World!`

## Updating port
As we will create many services during the workshop, we need to have unique ports to avoid port conflicts.
This can be solved better using infrastructure stacks, but we will keep it simple now.
1. Open `src/resources/application.yml`. Add
```yaml
server:
  port: ###
```
where ### is a unique port value, pick something between 10000 - 60000.

2. Rerun main#WorkshopApplication, close the previous running instance. IntelliJ tip: You can set the Run Configuration to `Single instance only`
which will automatically kill previous instance and create a new one.

3. Open a browser and visit `http://localhost:###/hello`. It should function like before.

## Enabling registration in Eureka
We will now register our application in Eureka so other services can find our machine. To register in Eureka, Eureka needs to know our application name, so that
other can request for our service.

1. Add a maven dependency to Eureka. In `pom.xml` inside `<dependencies>`
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

2. Add a maven dependency management for Eureka. In `pom.xml` between `<dependencies>` and `<build>` 
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Camden.SR3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

3. In `application.yml`, add configuration for Eureka server endpoint
```yaml
eureka:
  instance:
    preferIpAddress: true
  client:
    enabled: true
    serviceUrl:
      defaultZone: http://###:8761/eureka/
```
where ### is the ip to the Eureka server. Currently, the IP is `54.194.242.215`.

4. Still in `application.yml`, specify the application name
```yaml
spring:
  application:
    name: "My Client Name"
```
Change `My Client Name` to something appropriate.

5. Lastly, we need to enable the Eureka client in our Java code. Open `WorkshopApplication` and annotate the class with `@EnableEurekaClient`, i.e.
```java
@EnableEurekaClient
@SpringBootApplication
public class WorkshopApplication {
    //...
}
```

6. Restart the application. You should now find it on the Eureka server. Open a browser and visit `http://<EUREKA_SERVER_IP>:8761`. 
It might take a while for the service (up to ~30s given no exception in console out), once you have seen it pop up, continue.

##

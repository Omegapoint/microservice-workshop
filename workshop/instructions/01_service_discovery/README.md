Service discovery
-----------------
This part will show how to enable service discovery in your system. The goal of this part is to:
1. Connect and register to Eureka.
2. Retrieve list of available services from Eureka
3. Communicate with a service without knowing the address to it.

# Setup
Boilerplate code can be found under src in the root folder "workshop". 
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
where ### is a unique port power, pick something between 10000 - 60000.

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
    
        ....
    
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

3. In `application.yml`, add configuration for Eureka server endpoint. 
@master.dns@ will be replaced with the url to the master server running eureka when the project is built using a maven plugin called maven-resources-plugin.    
```yaml
eureka:
  client:
    enabled: true
    serviceUrl:
      defaultZone: http://@master.dns@:8761/eureka
```

4. Still in `application.yml`, specify the application name. 
The artifactId from the pom.xml will be used as application name when registering in eureka.
```yaml
spring:
  application:
    name: @artifactId@
```

5. Update your pom.xml to have an unique artifactId as this will be used to identify your service. Change 
```xml
<artifactId>my-cool-service-name</artifactId>
```

6. Lastly, we need to enable the Eureka client in our Java code. Open `WorkshopApplication` and annotate the class with `@EnableEurekaClient`, i.e.
```java
@EnableEurekaClient
@SpringBootApplication
public class WorkshopApplication {
    //...
}
```

7. Restart the application. You should now find it on the Eureka server. Open a browser and visit `http://<EUREKA_SERVER_IP>:8761`. Ask for the IP if you don't know it. 
It might take a while for the service (up to ~30s given no exception in console out), once you have seen it pop up, continue.

## Talk to other service
We will now talk to other services with only knowing the service name.

### Ping!
1. Add a dependency to Ribbon. Ribbon handles automatic name lookup in Eureka when performing requests.
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```

2. Add an interceptor to RestTemplate, telling it to do name lookups with Ribbon. In WorkshopApplication, add inside the `WorkshopApplication` class
```java
@Configuration
public static class ApplicationConfig {
    @Bean
    public RestTemplate restTemplate(final LoadBalancerClient loadBalancerClient) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new LoadBalancerInterceptor(loadBalancerClient));
        return restTemplate;
    }
}
```

3. Make a call to a known service. Running services can be found in Eureka. There should be a SuperHeroService which has the `GET` endpoint `/ping`
Either create a new HTTP endpoint or use the existing helloWorld#HelloWorldController method.
```java
@Autowired
RestTemplate restTemplate;

@RequestMapping("pingpong")
public String pingPong() {
    ResponseEntity<String> result = restTemplate.getForEntity("http://superhero-service/ping", String.class);

    return "Received from service: " + result.getBody();
}
```

4. Restart the application. Open a browser and visit your endpoint (e.g. `http://localhost:###/pingpong` according to above).
You should receive a proper response from the other service. If nothing is returned, check your console if any errors has occurred.

### Proper messages
While ping pong is great, lets retrieve a proper message. RestTemplate is built in Jackson mapping, so we will receive an JSON response and map it into a bean. 
The SuperHeroService also has an `GET` endpoint `/superhero`. Let´s call it and map it to a response model.

1. Create an response model, `SuperHeroResponse`
```java
public class SuperHeroResponse {
  public final String name;
  public final Galaxy galaxy;
  public final List<Power> powers;

  @JsonCreator
  public SuperHeroResponse(@JsonProperty("name") final String name,
                           @JsonProperty("galaxy") final Galaxy galaxy,
                           @JsonProperty("powers") final List<Power> powers) {
    this.name = name;
    this.galaxy = galaxy;
    this.powers = powers;
  }

  public static class Galaxy {
    public final String name;

    @JsonCreator
    public Galaxy(@JsonProperty("name") final String name) {
      this.name = name;
    }
  }

  public static class Power {
    public final String name;

    @JsonCreator
    public Power(@JsonProperty("power") final String name) {
      this.name = name;
    }
  }
}
```

2. Make a call to the super hero service. 
```java
@RequestMapping("hero")
public String myHero() {
    ResponseEntity<SuperHeroResponse> result = restTemplate.getForEntity("http://superhero-service/superhero", SuperHeroResponse.class);
    final SuperHeroResponse response = result.getBody();

    return String.format("Received hero %s with %d powers", response.name, response.powers.size());
}
```

3. Restart the application. Open a browser and visit your endpoint (e.g. `http://localhost:###/hero` according to above).
You should receive a randomly generated hero!

# Extra
Only these tasks if you are ahead and are only waiting for the next step.

### Retrieving all available services.
Ribbon does some magic in the background when replacing a service name to an IP using Eureka. Ribbon does not performa lookup on each call, that would be too expensive.
Instead, it stores a local cache of all available services. You can manually get all available services and inspect them yourself. Autowire in the `DiscoveryClient` and
call `getApplication()`

**Note!** We have not prepared this part. You might need to instantiate a DiscoveryClient bean.

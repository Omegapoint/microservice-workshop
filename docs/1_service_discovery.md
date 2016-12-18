# Service discovery
This part will show how to enable service discovery in your system. The goal of this part is to:
1. Connect and register to Eureka.
2. Retrieve list of available services from Eureka
3. Communicate with a service without know address to it.

## Setup
Boilerplate code can be found in the folder "start/1_service_discovery". 
The code consists of a clean Spring Boot application.

## Solutions
Solutions can be found in the folder "solutions/1_service_discovery"

# Steps

## Verify everything is starting
### In editor
Open the project in your editor. Run the main method in ClientApplication.
 
### In terminal
```sh
mvn package && java -jar target/channel-1.0.0.jar
```

Test the application by opening a browser and go to http://localhost:8080/hello

### Enabling Eureka.
Study the pom.xml to see what dependencies we have. What library includes Eureka?

You now need to enable registration to the Eureka server.
1. Add an annotation in the main class which enables Eureka. (@EnableEurekaClient)
2. Add properties that specifies where the Eureka server is in `application.yml`:
```yaml
 eureka:
   client:
     enabled: true
     serviceUrl:
       defaultZone: http://<IP TO EUREKA>:8761/eureka/
```
3. Update application name in the `application.yml`. Your service will register with this name,
so pick something that won't have conflicts.
4. Update the server port to something that others wont use.

Start your application and verify that no exceptions are being thrown. Open a browser and go to the 
Eureka server interface (http://<IP TO EUREKA>:8761/eureka/). Can you see your service?

### Retrieving available services.
You can retrieve all available service from the Eureka server. In order to do that:
1. Autowire an DiscoveryClient in your code (for example in a controller).
2. Try to find an method that will retrieve all applications.

### Communicating with other services.
You will now try to communicate with other services. To do this, we will use a framework called `ribbon`.
Documentation about ribbon can be found here `https://github.com/Netflix/ribbon

1. We need to add more spring-boot-cloud dependencies. Add the following
 * spring-cloud-starter-ribbon
 * spring-retry
 Please read up on what these do.
 
2. Add an interceptor to the RestTemplate bean. The RestTemplate has basic HTTP functionality to call URIs.
 We can enhance the RestTemplate by adding an interceptor that replaces service names with exact ip address by calling Eureka 
 server first. Adding the interceptor can be done by creating the RestTemplate ourselves and beanify it.
 * Create an class with a @Configuration annotation
 * Add a @Bean method that returns a RestTemplate
 * Add a LoadBalancerInterceptor to the new RestTemplate. The LoadBalancerInterceptor requires a LoadBalancerClient in its
 constructor, this can be injected into the method.
 
3. Finally, make a call to another service, only knowing the name of the service. In other words, you should not specify
the exact address to the other service. Autowire in your RestTemplate somewhere (perhaps in a controller) and use one
of its methods to call the service. To find a service, talk to one of your colleagues that are also creating a service.
There will also be one dummy service available, please ask your teachers for it.
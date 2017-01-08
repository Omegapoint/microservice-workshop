Provisioning
-------
When building a microservice architecture it quickly becomes tiresome to manage deployment of all the different services. 
A modern way of simplifying the process is to build a cluster of machines that can run any application and let a provisioning tool take care of deploying to this cluster. 

In this workshop we will use Docker, Docker registry and Rancher for this purpose. 

###Docker
Docker is a tool for packaging everything that is required to run an application inside a container. 
The containerized application can run on any machine with docker installed, without requiring dependencies such as java, maven, and so on.

###Docker Registry
Docker registry is simply a server side application that stores and lets you distribute Docker images. Dockerhub is an example of a public docker registry. 
We will use registry to upload our docker images to the amazon cloud.

###Rancher
Rancher is a tool used for orchestrating deployment of docker images. It has a graphical interface where you can control which docker images to deploy and where.
 
##Building and uploading a docker image
First up, we will build a docker image of our application by using a maven plugin called docker-maven-plugin.
 
Add the plugin to pom.xml
```xml
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.4.12</version>
    <configuration>
        <imageName>${master.ip}:5000/${artifactId}</imageName>
        <baseImage>java</baseImage>
        <entryPoint>["java", "-jar", "-Dspring.profiles.active=prod", "/${project.build.finalName}.jar"]
        </entryPoint>
        <!-- copy the service's jar file from target into the root directory of the image -->
        <resources>
            <resource>
                <targetPath>/</targetPath>
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
            </resource>
        </resources>
    </configuration>
</plugin>
```

A few things to note about the plugin configurations:
* We're using java as a base image, which means that java will be installed within our docker image.
* We're prefixing the image name with '${master.ip}:5000', which is the path to our docker registry server.
* The artifactId from pom.xml will be used as the image name.
* The entry point of the docker image will start our application with the production profile.

To build and upload the application, simply find your way to the workshop folder in a terminal and run:

```bash
mvn clean package docker:build -DpushImage
```

The application should now be uploaded to the Docker Registry. To verify, run:

```bash
curl -X GET <REGISTRY_IP>:5000/v2/_catalog
```

The ip should be visible in the logs when the image was uploaded to registry, if you cannot find it ask your teacher. 

Now hold your horses, we will not deploy your application to the cluster just yet. We have some stuff to take care of first. 

##Service Discovery within docker and AWS
Using service discovery within docker containers will not work out of the box, because the application is going to register an internal docker ip to eureka which no other server can reach.
In order to fix this we need some eureka config and aws magic. Add the following code to `WorkshopApplication.java`:
  
```java
@Value("${server.port}")
public int serverPort;

/**
 * Uses the public ip of the host machine when registering to Eureka.
 * This is required when running application with docker
 */
@Bean
@Profile("prod")
public EurekaInstanceConfigBean eurekaInstanceConfig() {

    EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(new InetUtils(new InetUtilsProperties()));
    AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
    config.setDataCenterInfo(info);
    info.getMetadata().put(AmazonInfo.MetaDataKey.publicHostname.getName(),
            info.get(AmazonInfo.MetaDataKey.publicHostname));
    config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
    config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicHostname));
    config.setNonSecurePort(24701);
    return config;
}
```

When running the `prod` profile, as done within the docker image, the application will use a library from Amazon to find the public ip of the machine and then use it to register to eureka. 

The code is now ready for release. Build and upload it again using: 

```bash
mvn clean package docker:build -DpushImage
```

##Provisioning with Rancher
It's time to ship your application to the cloud. 

Go to the url: http://<RANCHER_IP>:8080 to get to Rancher. Let's have a quick look around.

###Infrastructure 
Here you can find all components setting up the infrastructure of the provisioning. Under hosts you will find all the servers available for deployment of applications. 

###Stacks
Under stacks you will find information about groups (stacks) of applications. For example, under infra-services are the services running that you previously communicated with in your application.
 
###Let's deploy
Go to Stacks - All and press "Add Stack". Fill in a proper name and description of your stack. 
This name should indicate which kind of application you are running, as application with similar purpose preferably should end up in the same stack.

Click Create.

Now let's add your service to the stack. Fill in the following:

    * Scale: Choose on how many machines your application should run. Pick a reasonable number. 
    * Name: <artifactId> (Replace with the artifactIf used in your pom)
    * Description: Description of your application
    * Select image: 172.31.12.100:5000/<artifactId> (Replace with the artifactIf used in your pom). The ip indicates to rancher where the registry is located. 
    * Port map: <APPLICATION_PORT> -> <APPLICATION_PORT> (Replace with the port where your application is running)
     
Click Create.

Your application is now starting. Click on the application name to get more information. Have a look around. Try to find the logs of your java application from here.

You can also find the public ip of any of the machines running your application to try and access it. Ask the teacher if you need help finding it. 

##Upgrading your application
Make some changes to your application. Maybe add an endpoint that talks to one of your colleagues applications.
 
Build and upload a new docker image as done previously.

Go to your stack in Rancher and click the three dots to the far right of your application. Choose upgrade.

Click Create.

Your application will now be upgraded with your new docker image. 
 
 
 
 
 
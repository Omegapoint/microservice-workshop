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
* We're prefixing the image name with `${master.ip}:5000`, which is the path to our docker registry server.
* The artifactId from pom.xml will be used as the image name. This name will be used later when deploying the application.
* The entry point of the docker image will start our application with the production profile.

To build and upload the application, simply find your way to the workshop folder in a terminal and run:

```bash
./mvnw clean package docker:build -DpushImage
```

The application should now be uploaded to the Docker Registry. To verify, run the following command to list all images in the registry:

```bash
curl -X GET <MASTER_IP>:5000/v2/_catalog
```

The ip to Docker Registry should be visible in the logs when the image was uploaded to registry, if you cannot find it ask your teacher.

Your application is now packaged as a docker image, but we will not deploy your application to the cloud cluster just yet. We have some stuff to take care of first. 

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
    config.setNonSecurePort(serverPort);
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

Go to the url: http://[MASTER_IP]:8080 to get to Rancher. Let's have a quick look around.

####Infrastructure 
Under the infrastructure tab you will find all components setting up the infrastructure of the provisioning. Under hosts you can see all the servers available for deployment of applications. 

####Stacks
Under the stacks tab you will find information about stacks (groups) of applications. For example, under infra-services are all the services running that you previously communicated with in your application.
 
###Let's deploy
1. Go to `Stacks -> All` and press `Add Stack`. 
2. Fill in a proper name and description of your stack. 
This name should indicate which kind of application you are running. Application with similar purpose should preferably end up in the same stack. Our stack will only contain one application.
Click `Create`.

3.  Now let's add your service to the stack. Click `Add service`
Fill in the following:
* Scale: Choose on how many machines your application should run. Pick one for now.
* Name: <artifactId> (Replace with the artifactId used in your pom)
* Description: Description of your application. Can contain whitespace.
* Select image: 172.31.12.100:5000/##artifactId### (Replace with the ###artifactId### used in your pom). The ip indicates to rancher where the registry is located. 
* Port map: Replace both sides with the port where your application is running)
Click Create.

4. Your application is now being deployed to the cluster. Click on the application name to get more information. 
Have a look around. Try to find the logs of your java application from here. The service is deployed when the status has changed to `Active`

5. You can also find the public ip of any of the machines running your application to access it. It should be listed under `Ports` 
Ask the teacher if you need support. 

##Upgrading your application
1. Make some simple changes to your application. Maybe add an endpoint that talks to one of your colleagues applications.
 
2. Build and upload a new docker image as done previously (maven command).

3. Go back to your stack in Rancher.
 
4. Click the three dots to the far right of your service/application. Choose upgrade.

5. Dont change any values. Click Upgrade.

Your application will now be upgraded with your new docker image. Verify that your changes are working. **Please note** that the upgrade might take some additional time after being
classified as Upgraded. This is due to the fact that Spring Boot requires some additional time to start.

6. In the Rancher UI, press the dots again. Select `Finish upgrade`
 
# Extra

## Deploying available stacks
Rancher already has some preconfigured stacks. Try to provision a Wordpress instance and create a new blog entry.
Note! You probably need to retrieve a secret key when setting up Wordpress. This is a perfect opportunity to use a remote Rancher console to your docker container. Click around and find where you can start a console on your container.

## Registering a slave

A cluster is only good if it has enough slaves. It seems like our system park does not have enough due to so many services. Let's provision some new!

It is simple to create a new AWS slave. Login in to AWS:

1. Access the AWS Console

2. Go to EC2 

3. Go to correct region (Ireland)

4. Click `Launch instance`

5. Select `Ubuntu Server 14.04 LTS (HVM)`, click `Next`.

6. Select an instance type equal to or greater than `small`, click `Next`.

7. Under Advanced details, add the contents from `setup.sh` found in https://github.com/Omegapoint/microservice-workshop/blob/master/infrastructure/slaves/setup.sh to the text area (keep `As text` checked)
Modify the content `MASTER_IP` to the private ip of the master instance. Click `Next`.

8. Add some storage. Note a machine can run multiple services which means multiple images. Pick something between 30 - 40gb. Click `Next`.

9. Add tags. Name should symbolise slave. Add an `Owner` tag and enter your name. Click `Next`.

10. Time for security groups. Select existing and pick `micro-workshop-users`. If you pick wrong, the slave won´t be able to communicate with other services. Click `Next`

11. On review page, review and click `Launch`.

12. Pick an existing key pair or create a new. Click `Launch`

The instance will now get created and connect as a slave to Rancher automatically. Wait for it to initialize (look at AWS Console). Once ready, you should be able to find the machine in Rancher under `Infrastructure/Hosts`

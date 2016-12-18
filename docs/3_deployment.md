# Deployment
As the overall system grows, so does the complexity of deploying services. It's no longer feasible to manually
 copy and restart JAR file on multiple machines. Instead, we use systems built for deploying this kind of architecture.
 
There are many options available, OpenShift, Kubernetes, Docker Swarm etc. We will use a system called **Rancher**, which abstracts away
the actual deploying system (Kubernetes, Docker Swarm etc.) and also the machine deployment (Amazon, Azure, DigitalOcean).

In order to be system independent and minimize dependency requirements, we will deploy our applications in a Docker container.
The docker container is uploaded to our own registry and can be deployed using Rancher.

# BINGOLINGO
EINE PICTUREEE POR FÄVÅR
# BINGOLINGO

## Setup
This part requires a complete solutions from the previous part `logging`. 
Copy to solutions if you have not finished that part.

## Solutions
Solutions can be found in the folder "solutions/3_rancher"

# Steps

## Service discovery within Docker
Due to running our services inside Docker containers, the IP address registered by our application will point
to our Docker IP instead of our machine IP. This is a quite difficult problem to solve, but we can take advantage
of knowing that our slaves will be running in the Amazon cluster. AWS has a name lookup server which you can ping and receive
back the IP address of your calling server. We will do this during the startup of our application and register that IP address instead.

```java
@Bean
public EurekaInstanceConfigBean eurekaInstanceConfig() {
	EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(new InetUtils(new InetUtilsProperties()));
	AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
	config.setDataCenterInfo(info);
	info.getMetadata().put(AmazonInfo.MetaDataKey.publicHostname.getName(),
			info.get(AmazonInfo.MetaDataKey.publicIpv4));
	config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
	config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicIpv4));
	config.setNonSecurePort(#SERVICE_PORT#);
	return config;
}
```

Update the `#SERVICE_PORT#` to your port.

## Packaging application in Docker container
When running the `mvn clean package docker:build` command, we also want it to build a Docker container. The maven plugin 
docker-maven-plugin written by Spotify does this for us. Add the plugin to the pom according to
```xml
<plugin>
	<groupId>com.spotify</groupId>
	<artifactId>docker-maven-plugin</artifactId>
	<version>0.4.12</version>
	<configuration>
		<imageName>#REGISTRY_IP#:5000/#SERVICE_NAME#</imageName>
		<baseImage>java</baseImage>
		<entryPoint>["java", "-jar", "/${project.build.finalName}.jar"]</entryPoint>
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

1. Update the #REGISTRY_IP# to point to the correct IP.
2. Update the #SERVICE_NAME# to match your service name. The docker container will be named
according to this.

In order to be allowed to push to our registry, it needs to be added as an insecure registry. 
* For Mac: Open your Docker for Mac UI (taskbar) and "Preferences". Go to "Advanced" and add the insecure registry (both ip and port) 
* For Windows: Open your Docker for Windows UI (taskbar) and "Preferences". Go to "Advanced" and add the insecure registry (both ip and port)
* For Linux: Edit `/etc/default/docker` and add `DOCKER_OPTS="--insecure-registry myregistrydomain.com:5000"

Running `mvn clean package docker:build` will produce the Docker container. Add the flag `-DpushImage to
 push the image to the registry. (i.e. `mvn clean package docker:build -DpushImage`)

## Deploying in Rancher
The rancher cluster consists of a master machine which contains Rancher Master and a Docker Registry.
It also contains several slaves on which the services will be deployed. We will now deploy our service using Rancher.

1. Go to the Rancher UI, the address will be given by your teachers.
2. Go to the page `Stack`
3. Click on `Add new service`
4. Yada yada


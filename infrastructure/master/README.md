This describes how to setup the master server.

# Start setup
Time to launch a new instance on AWS. Add Userdata (step 2) with the contents from setup.sh

# Docker
Docker is installed in the inital setup script (setup.sh)

# Docker registry
Registry is also installed in the initial setup script. If not, you can start it with
```sh
sudo docker run -d -p 5000:5000 --name registry registry:2
```

# Rancher
The rancher master is also started by the setup script. If not, you can start it with
```sh
sudo docker run -d --restart=unless-stopped -p 8080:8080 rancher/serverv:1.3.0
```
In order for slaves to be able to register, we need to do some manual configuration. Open a browser and visit
```
http://<IP_OF_MASTER>:8080
```
Click on infrastructure and proceed to add a host. When asked for the register ip, overwrite this with
```
http://<PRIVATE_IP_IN_AMAZON>:8080
```
The private ip for the master node can be found in the AWS Console. Click next and once you have received the command for creating a slave, stop.
This command is already added in the slave instructions.

# ELK
Installs Elasticsearch, Logstash and Kibana. 

## Build docker image
Go into elk folder. Run
```sh
docker build . -t elk
``` 

Push the image to the master
```sh
docker push <IP_OF_MASTER>:5000/elk
```

## Run docker image
SSH into the master instance.
Run 
```sh
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 elk
```

# Eureka
This is a separate service which can be found in the `services` folder. 

# Build docker image
Run
```sh
mvn clean package docker:build -DpushImage
```

This will create a Docker image and push it to the master instance.

# Run Eureka
SSH into the master instance. 
Stop any running Eureka instance.
```sh
docker stop $(docker ps -a -q  --filter ancestor=localhost:5000/eureka-server)
```

Start with
```sh
docker run -p 8761:8761 localhost:5000/eureka-server
```
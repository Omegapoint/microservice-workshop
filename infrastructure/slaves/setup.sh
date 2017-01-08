#!/bin/bash

MASTER_IP="172.31.12.100"
sudo apt-get update
sudo apt-get install -y apt-transport-https ca-certificates
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" | sudo tee /etc/apt/sources.list.d/docker.list

sudo apt-get update
sudo apt-get install -y linux-image-extra-$(uname -r) linux-image-extra-virtual jq docker-engine

sudo usermod -aG docker ubuntu

# Allow insecure access to docker registry
echo "DOCKER_OPTS=\"--insecure-registry $MASTER_IP:5000\"" | sudo tee -a /etc/default/docker
sudo service docker restart

# Register slave with rancher master
REGISTRATION_URL=$(curl http://$MASTER_IP:8080/v2-beta/projects/1a5/registrationtokens?state=active | jq -r '.data[0].links.registrationUrl')
PUBLIC_IP=$(curl http://169.254.169.254/latest/meta-data/public-ipv4)
sudo docker run -e CATTLE_AGENT_IP="$PUBLIC_IP" -d --privileged -v /var/run/docker.sock:/var/run/docker.sock -v /var/lib/rancher:/var/lib/rancher rancher/agent:v1.1.0 $REGISTRATION_URL
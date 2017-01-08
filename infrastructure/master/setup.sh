#!/bin/bash

# Add docker repository
sudo apt-get update
sudo apt-get install -y apt-transport-https ca-certificates
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" | sudo tee /etc/apt/sources.list.d/docker.list

# Install Docker
sudo apt-get update
sudo apt-get install -y \
    linux-image-extra-$(uname -r) \
    linux-image-extra-virtual \
    docker-engine

# Enable running docker without sudo
sudo usermod -aG docker ubuntu

# Start Docker Registry
sudo docker run -d -p 5000:5000 --name registry registry:2

# Start rancher
sudo docker run -d --restart=unless-stopped -p 8080:8080 rancher/server

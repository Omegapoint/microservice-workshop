Setup
-----

Run this command for all services:
    mvn clean package docker:build -DpushImage

SuperHero service
-----------------
Running on port: 24701 
Endpoint: /ping, /superhero


SuperPower service
------------------
Running on port: 24702
Endpoint: /ping, /superpowers

Galaxy service
--------------
Running on port: 24703
Endpoints: /ping, /galaxy
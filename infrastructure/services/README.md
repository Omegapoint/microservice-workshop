Setup
-----

* Change IP for docker plugin in pom.xml for all services

* Run mvn clean package docker:build -DpushImage

Employee service
-----
Running on port: 24701 
Endpoint: /employee


Team service
-----
Running on port: 24702
Endpoint: /teams

CompetenceDay service
-----
Running on port: 24703
Endpoints: /competencedays
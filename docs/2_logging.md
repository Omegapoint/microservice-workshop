# Logging
An important piece of microservice architecture is proper logging. We need to know when and where requests
have travelled, and possible died.

In this part, we will use the ELK (elasticsearch, logstash, kibana) as our logging infrastructure. Each service
will send all its logging to this.

## Setup
This part requires a complete solutions from the previous part `service discovery`. Copy to solutions if you have not
finished that part.

## Solutions
Solutions can be found in the folder "solutions/2_logging"

# Steps
## Sending logs to ELK
We will now send our logging to the ELK stack instead printing it to the console.
As a fine feature of Spring Boot, we will need to override some dependencies and add some new
1. Specify the logback version to 1.1.6 in the properties part of the pom.
2. Add a dependency to logstash-logback-encoder

We now need to update our logback config to send to ELK. This can be done by creating a new appender
1. Create a new tcp logstash appender. 
Information can be found on `https://github.com/logstash/logstash-logback-encoder` 
The destination ip will be given to you by the teachers.

GÖR DETTA ENKLARE

If you get mighty stuck, please consult the solutions for inspiration. When the logging is configured, write some logs and verify
that you can find them under 'Discover' in Kibana. It can be a wise choice to filter based on your application name.  

## Adding service tracing to logs
In order to properly troubleshoot a system, we need to know how the request has travelled and when it failed. 
Google gave out a paper regarding their system Dapper which is an distributed systems tracing infrastructure. The concept is to automatically
add a `Span` id and a `Trace` id to each incoming request. The Span id is kept when the request is send further one while the Trace id is regenerated per service.
LÄGG TILL BILD HÄR

Spring has created their own version of Dapper, called Sleuth.

1. Add new dependency (again) in the pom to Sleuth. `spring-cloud-starter-sleuth`
2. Update the console logging pattern in `logback.xml` to include the span and trace id.
`[%X{X-B3-SpanId},%X{X-B3-TraceId}]` is a good pick.
This information is automatically added to the tcp appender, so the information is directly sent to ELK without modifications.
3. Do some logging in your system and verify that the IDs are printend in your console and sent to ELK.

Sleuth has functionality to automatically add the IDs to RestTemplate calls. Make a request to a service with Sleuth enabled and that makes some logging.
In Kibana, filter out your Span id and check the path the request has travelled through all systems.

DET BEHÖVS BILDER HÄR. BEHÖVER ÄVEN VARA ENKLARE

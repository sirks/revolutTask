# Revolut Backend Test
Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

## Tools

* gradle
* guice
* jooq
* jetty
* spock

## Setup
__Note:__ If you do not have gradle installed use `./gradlew`      
Run: `gradle clean build`

To start the app from command line:  
`java -jar build/libs/revolutTask-1.0-SNAPSHOT-all.jar`

p.s.Some transaction concurrency issues not implemented.
[![Quick Build](https://github.com/schemacrawler/SchemaCrawler-Web-Application/workflows/Quick%20Build/badge.svg)](https://github.com/schemacrawler/SchemaCrawler-Web-Application/actions?query=workflow%3A%22Quick+Build%22)
[![codecov](https://codecov.io/gh/schemacrawler/SchemaCrawler-Web-Application/branch/master/graph/badge.svg)](https://app.codecov.io/gh/schemacrawler/SchemaCrawler-Web-Application)

<img src="https://raw.githubusercontent.com/schemacrawler/SchemaCrawler/master/schemacrawler-website/src/site/resources/images/schemacrawler_logo.png" height="100px" width="100px" align="right" />

# SchemaCrawler Web Application

> **Please see the [SchemaCrawler website](https://www.schemacrawler.com/) for more details.**


## Web Application

> To access the application, open a browser to
[https://schemacrawler-webapp.herokuapp.com/](https://schemacrawler-webapp.herokuapp.com/)

SchemaCrawler Web Application makes SchemaCrawler accessible on the web. You can generate a schema diagram of your SQLite database.

-----

## Technologies

This is a Spring Boot web application with a Bootstrap user interface, with source code control in GitHub, which is automatically built on every commit by GitHub Actions using a Maven build, tests are run, and coverage measured with JaCoCo and Codecov.io, and then immediately deployed to Heroku using a Docker image, which generates an crows-foot ERD of a SQLite database.


## Build and Run

### Build

- Install [Graphviz](https://www.graphviz.org), which is a prerequisite for SchemaCrawler
- Install Docker
- Build application from Maven, run `mvn clean package`


### Build Docker Image

- Follow the steps above
- Install Docker
- Build application and Docker image from Maven, run `mvn -Ddocker.skip=false clean package`


### Start the Server

Do one of the steps below to start the web application locally on your system:
- Start the application from Maven, run `mvn -Dspring-boot.run.fork=false -Dspring.profiles.active=local spring-boot:run`
- Start application from the jar file, run 
  `java -DAWS_ACCESS_KEY_ID=xxxxx -DAWS_SECRET=xxxxx -DAWS_S3_BUCKET=xxxxx -jar target/schemacrawler-webapp-16.16.3.5.jar`
  or, in local mode,
  `java -Dspring.profiles.active=local -jar target/schemacrawler-webapp-16.16.3.5.jar`
- Start the application from the local image in a Docker container, run  `docker run -d --rm --env AWS_ACCESS_KEY_ID=xxxxx --env AWS_SECRET=xxxxx --env AWS_S3_BUCKET=xxxxx -p 8080:8080 -t schemacrawler/schemacrawler-webapp`
  or, in local mode,
  `docker run --rm --env SPRING_PROFILES_ACTIVE=local -p 8080:8080 -t schemacrawler/schemacrawler-webapp`


### Use the Application

#### From a Browser

Then, after you ensure that the web server is running, either from the command-line,
or the Docker container, open a browser to
[https://localhost:8080](https://localhost:8080)

#### As an API

You can use the SchemaCrawler web application as an API. 

Submit a request and your SQLite database file (say "test.db" in your local directory) with a command like this:
```sh
curl \
  -F "name=Sualeh Fatehi" \
  -F "email=sualeh@hotmail.com" \
  -F "file=@test.db" \
  -H "Accept: application/json" \
  https://schemacrawler-webapp.herokuapp.com/schemacrawler
```
You will get a 12 character key in return, which uniquely identifies your request.

If your identifier is "l0nk0wu4t2a3", can retrieve the generated image after a few minutes using a request similar to:
```sh
curl https://schemacrawler-webapp.herokuapp.com/schemacrawler/results/l0nk0wu4t2a3/diagram > diagram.png
```

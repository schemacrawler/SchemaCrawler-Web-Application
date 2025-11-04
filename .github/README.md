**:star: Star it :arrow_heading_up: if you love it!**

[![Quick Build](https://github.com/schemacrawler/SchemaCrawler-Web-Application/workflows/Quick%20Build/badge.svg)](https://github.com/schemacrawler/SchemaCrawler-Web-Application/actions?query=workflow%3A%22Quick+Build%22)
[![Integration Tests](https://github.com/schemacrawler/SchemaCrawler-Web-Application/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/schemacrawler/SchemaCrawler-Web-Application/actions/workflows/integration-tests.yml)
[![codecov](https://codecov.io/gh/schemacrawler/SchemaCrawler-Web-Application/branch/main/graph/badge.svg)](https://app.codecov.io/gh/schemacrawler/SchemaCrawler-Web-Application)

<img src="https://raw.githubusercontent.com/schemacrawler/SchemaCrawler/main/schemacrawler-website/src/site/resources/images/schemacrawler_logo.png" height="100px" width="100px" align="right" />

# SchemaCrawler Web Application

> **Note**: Please see the [SchemaCrawler website](https://www.schemacrawler.com/) for more details.


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

- Set the following environmental variables locally
  - AWS_ACCESS_KEY_ID
  - AWS_SECRET_ACCESS_KEY
  - AWS_S3_BUCKET
- Do one of the steps below to start the web application locally on your system
  - Start the application from Maven, run  
	  `mvn -Dspring-boot.run.fork=false spring-boot:run`
  - Start application from the jar file, run  
	  `java -jar target/schemacrawler-webapp-17.1.4-1.jar`
  - Start the application from the local image in a Docker container, run  
	  `docker run -d --rm --env AWS_ACCESS_KEY_ID=xxxxx --env AWS_SECRET_ACCESS_KEY=xxxxx --env AWS_S3_BUCKET=xxxxx -p 8080:8080 -t schemacrawler/schemacrawler-webapp`


### Use the Application

Then, after you ensure that the web server is running, either from the command-line,
or the Docker container, open a browser to
[https://localhost:8080](https://localhost:8080)

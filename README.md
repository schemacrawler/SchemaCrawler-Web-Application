[![Build Status](https://travis-ci.org/schemacrawler/SchemaCrawler-Web-Application.svg?branch=master)](https://travis-ci.org/schemacrawler/SchemaCrawler-Web-Application)
[![Coverage Status](https://coveralls.io/repos/github/schemacrawler/SchemaCrawler-Web-Application/badge.svg)](https://coveralls.io/github/schemacrawler/SchemaCrawler-Web-Application)

# ![SchemaCrawler](https://github.com/schemacrawler/SchemaCrawler/raw/master/schemacrawler-docs/logo/schemacrawler_logo.png?raw=true) SchemaCrawler Web Application

> **Please see the [SchemaCrawler website](http://www.schemacrawler.com/) for more details.**


## Web Application

> To access the application, open a browser to
[https://schemacrawler-webapp.herokuapp.com/](https://schemacrawler-webapp.herokuapp.com/)

SchemaCrawler Web Application makes SchemaCrawler accessible on the web. You can generate a schema diagram of your SQLite database.

-----

## Technologies

This is a Spring Boot 2.1 web application with a Bootstrap user interface, with source code control in GitHub, which is automatically built on every commit by Travis CI using a Maven build, tests are run, and coverage measured with JaCoCo and Coveralls, and then immediately deployed to Heroku using a Docker image, which generates an crows-foot ERD of a SQLite database.


## To Build and Run

### Build

Install [Graphviz](http://www.graphviz.org), which is a prerequisite for SchemaCrawler

Modify `schemacrawler.webapp.storage-root` in `src/main/resources/application.properties` 
to point to a temporary directory on your system. 

Build and start application from Maven, run
```
mvn clean package
```

### Start the Server

Start the application from Maven, run
```
mvn spring-boot:run
```

Start application from the jar file, run
```
java -jar target/schemacrawler-webapp-15.01.06.01.jar
```

### Use the Application

Then, after you ensure that the web server is running, either from the command-line,
or the Docker container.

To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)


## Docker Container

### To Create Docker Image 

To create the Docker image, and push it to Docker Hub, run
```
docker build -t schemacrawler/schemacrawler-webapp .
docker push schemacrawler/schemacrawler-webapp
```

Run the local image in a Docker container, using
```
docker run -p 8080:8080 -t schemacrawler/schemacrawler-webapp
```

To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)


### To Host Docker Image on Heroku

Follow instructions on [Container Registry & Runtime - Docker Deploys](https://devcenter.heroku.com/articles/container-registry-and-runtime).

To access the application, open a browser to
[https://schemacrawler-webapp.herokuapp.com/](https://schemacrawler-webapp.herokuapp.com/)

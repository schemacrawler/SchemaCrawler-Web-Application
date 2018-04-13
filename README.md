[![Build Status](https://travis-ci.org/sualeh/SchemaCrawler-Web-Application.svg?branch=master)](https://travis-ci.org/sualeh/SchemaCrawler-Web-Application)
[![Coverage Status](https://coveralls.io/repos/sualeh/SchemaCrawler-Web-Application/badge.svg?branch=master&service=github)](https://coveralls.io/github/sualeh/SchemaCrawler-Web-Application?branch=master)

# SchemaCrawler Web Application

-----

## Web Application

This is a Spring Boot web application with a Bootstrap user interface, with source code control in GitHub, which is automatically built on every commit by Travis CI using a Maven build, tests are run, and coverage measured with JaCoCo and Coveralls, and then immediately deployed to Heroku using a Docker image, which generates an crows-foot ERD of a SQLite database.

To access the application, open a browser to
[https://schemacrawler-webapp.herokuapp.com/](https://schemacrawler-webapp.herokuapp.com/)

-----

## To Build and Run

Install [Graphviz](http://www.graphviz.org), which is a prerequisite for SchemaCrawler

Modify `schemacrawler.webapp.storage-root` in `src/main/resources/application.properties` 
to point to a temporary directory on your system. 

Build and start application from Maven, run
```
mvn clean package spring-boot:run
```

Start application from the jar file, run
```
java -jar target/schemacrawler-webapp-14.20.04.01.jar
```

-----

## To Use the Web Application 

### Run Locally

Ensure that the web server is running, either from the command-line,
or the Docker container.

To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)

-----

## To Create Docker Image 

To create the Docker image, and push it to Docker Hub, run
```
docker build -t sualeh/schemacrawler-webapp .
docker push sualeh/schemacrawler-webapp
```

Run the local image in a Docker container, using
```
docker run -p 8080:8080 -t sualeh/schemacrawler-webapp
```
To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)

-----

## To Host Docker Image on Heroku

Follow instructions on [Container Registry & Runtime - Docker Deploys](https://devcenter.heroku.com/articles/container-registry-and-runtime).

To access the application, open a browser to
[https://schemacrawler-webapp.herokuapp.com/](https://schemacrawler-webapp.herokuapp.com/)

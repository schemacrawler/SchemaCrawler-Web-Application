# SchemaCrawler Web Application

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
java -jar target/schemacrawler-webapp-14.17.04.02.jar
```

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

-----

## To Use the Web Application 

### Run Locally

Ensure that the web server is running, either from the command-line,
or the Docker container.

To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)

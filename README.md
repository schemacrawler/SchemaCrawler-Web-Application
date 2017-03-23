# SchemaCrawler Web Application

-----

## To Build and Run

Build and start application from Maven, run
```
mvn clean package spring-boot:run
```

Start application from the jar file, run
```
java -jar target/webapp-0.0.1-SNAPSHOT.jar
```

-----

## To Create Docker Image 

To create the Docker image, and push it to Docker Hub, run
```
mvn -DskipTests -DpushImage clean package docker:build 
```

Run the local image in a Docker container, using
```
docker run -p 8080:8080 -t sualeh/schemacrawler-webapp
```

-----

## To Use the Web Application 

Ensure that the web server is running, either from the command-line,
or the Docker container.

To access the application, open a browser to
[http://localhost:8080](http://localhost:8080)

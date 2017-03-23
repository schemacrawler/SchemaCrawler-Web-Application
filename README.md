# SchemaCrawler Web Application

-----

## To Build and Run

```
mvn clean package spring-boot:run
```

From the jar file,
```
java -jar target/webapp-0.0.1-SNAPSHOT.jar
```

-----

## To Create Docker Image 

To create the Docker image, and push it to Docker Hub, run
```
mvn -DskipTests -DpushImage clean package docker:build 
```

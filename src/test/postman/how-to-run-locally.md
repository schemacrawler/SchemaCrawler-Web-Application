# How to Run Postman Tests Locally

1. Install newman.

2. Build the web application.
```sh
mvn clean package
```

3. Start the server.
```sh
export AWS_ACCESS_KEY_ID=AKIA2YOURCCESSKEYFVM
export AWS_SECRET=eVyourpassword0zOOiYOURPASSWORDFII4VchI
export AWS_S3_BUCKET=schemacrawler-web-app-test-1
mvn spring-boot:run
```

4. Run tests in newman.
```sh
newman \
run \
  --verbose \
  --color on \
  --delay-request 1000 \
  --working-dir src/test/postman \
  --env-var "url=http://localhost:8080" \
  src/test/postman/schemacrawler-web-application.postman_collection.json
```

It is possible to run the tests with newman in Docker container, but local files are not found.
```
docker run -t --rm \
--mount type=bind,source="$(pwd)/src/test/postman",target=/etc/newman \
postman/newman \
run \
  --verbose \
  --color on \
  --delay-request 1000 \
  --working-dir src/test/postman \
  --env-var "url=http://host.docker.internal:8080" \
  schemacrawler-web-application.postman_collection.json
```

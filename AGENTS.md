# AGENTS.md — SchemaCrawler-Web-Application

A Spring Boot web application that accepts uploaded SQLite database files, generates an entity-relationship diagram using SchemaCrawler, stores the result, and optionally sends an email notification.

## Build and Test

```bash
# Build the JAR
mvn clean package

# Run locally (no Docker required)
mvn spring-boot:run -Dspring-boot.run.fork=false
# Access at http://localhost:8080

# Build Docker image
mvn -Ddocker.skip=false clean package

# Run via Docker (requires AWS environment variables)
docker run -d --rm \
  -e AWS_ACCESS_KEY_ID=... \
  -e AWS_SECRET_ACCESS_KEY=... \
  -e AWS_S3_BUCKET=... \
  -p 8080:8080 \
  schemacrawler/schemacrawler-webapp
```

## Technology Stack

- **Framework:** Spring Boot 4, Java 21
- **UI:** Bootstrap + Thymeleaf HTML templates
- **API spec:** OpenAPI/Swagger (`src/main/resources/api/schemacrawler-web-application.yaml`)
- **Testing:** JUnit 5, Spring Boot Test, Hamcrest, Testcontainers (LocalStack)

## Architecture

### Layer Structure

```
HTTP Request
    → Controller (DiagramRequestController / DiagramResultController)
    → ProcessingService  [async @Async]
        → StorageService (interface)         ← AmazonS3StorageService (prod)
        → NotificationService (interface)    ← AmazonSESNotificationService (prod)
        → SchemaCrawler library (diagram generation)
```

### Key Classes

| Package | Class | Role |
|---------|-------|------|
| `controller` | `DiagramRequestController` | Handles file upload form at `/schemacrawler` |
| `controller` | `DiagramResultController` | Serves generated diagrams at `/diagrams/{key}` |
| `service.processing` | `ProcessingService` | Orchestrates diagram generation asynchronously |
| `service.storage` | `StorageService` | Interface — pluggable storage backend |
| `service.storage` | `AmazonS3StorageService` | Production: AWS S3 via AWS SDK v2 |
| `service.notification` | `NotificationService` | Interface — pluggable notification backend |
| `service.notification` | `AmazonSESNotificationService` | Production: AWS SES via AWS SDK v2 |

### Async Processing

Diagram generation runs asynchronously (`@EnableAsync` in `AsyncConfiguration`) in a bounded thread pool (2–5 threads, 500 queue capacity). The controller returns immediately after queuing the job; the result page is loaded after the job completes.

### Pluggable Backends

`StorageService` and `NotificationService` are interfaces. Tests substitute `FileSystemStorageService` (stores files locally) and a logging `NotificationService` to avoid requiring live AWS access. Add new backends by implementing the interface — do not add conditional logic inside existing implementations.

## Test Structure

Tests are in `src/test/java/us/fatehi/schemacrawler/webapp/test/`:

| Test Class | Type |
|------------|------|
| `RequestControllerTest`, `ResultControllerTest` | Spring MVC slice tests |
| `ControllerRoundtripTest` | End-to-end request/response flow |
| `RequestControllerAPITest`, `ResultControllerAPITest` | OpenAPI specification validation via `swagger-request-validator` |
| `RequestControllerWithS3Test`, `LocalStackS3BucketTest` | S3 integration using Testcontainers LocalStack |

## Coding Guidelines

- Prefer **immutability**: use `final` on fields, parameters, and local variables.
- Implement `StorageService` or `NotificationService` to add new backends; keep existing implementations clean.
- Tests use **JUnit 5** with **Hamcrest** matchers; AWS integration tests use Testcontainers LocalStack.
- All dependency versions are managed in `schemacrawler-parent/pom.xml`; do not declare versions in the module POM.

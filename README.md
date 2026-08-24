# Spring Boot Online Store

REST API for an online store developed with Spring Boot.

The project implements product and category management, user authentication,
order processing and stock management. It also includes automated tests,
Docker support and a Kubernetes deployment.

## Technologies

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- H2
- JUnit 5
- Mockito
- Maven
- Docker
- Docker Compose
- Kubernetes
- OpenAPI / Swagger
- Spring Boot Actuator
- Git / GitHub

## Main Features

- User registration and authentication
- JWT-based authentication
- Role-based authorization
- Product management
- Category management
- Order creation
- Automatic stock management
- Order total calculation
- Exception handling and validation
- Unit testing with JUnit and Mockito
- API documentation with Swagger
- Application health monitoring with Actuator
- Dockerized application and database
- Kubernetes deployment

## Architecture

```text
Client
  |
  v
REST Controllers
  |
  v
Services
  |
  v
Repositories
  |
  v
MySQL
```

The application follows a layered architecture:

- Controller layer: REST endpoints
- Service layer: business logic
- Repository layer: data access
- Entity layer: persistence model
- DTO layer: API requests and responses
- Security layer: authentication and authorization

## API Documentation

When the application is running, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Health Check

Spring Boot Actuator provides:

```text
GET /actuator/health
```

Example response:

```json
{
  "status": "UP"
}
```

## Running the tests

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Tests use an H2 in-memory database, so a local MySQL instance is not required.

## Running with Docker

Create a `.env` file:

```env
MYSQL_ROOT_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

Then run:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

## Kubernetes

Kubernetes manifests are located in:

```text
k8s/
```

The deployment includes:

- Spring Boot Deployment
- MySQL Deployment
- Services
- ConfigMap
- Secrets
- PersistentVolumeClaim
- Readiness probe
- Liveness probe

Secret files are not committed to the repository. Example files are provided
to show the required configuration.

## Author

Developed as a backend portfolio project using Spring Boot and DevOps tools.

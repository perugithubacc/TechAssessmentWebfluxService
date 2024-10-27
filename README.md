# Tech Assessment Webflux Service

This is a Spring Boot application for managing customers and products. It leverages Maven for building and Docker Compose for managing services. The application is built using Java 17.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Building the Project](#building-the-project)

## Features

- Manage customers and products through a Webflux RESTful API.
- Added Redis for caching and Kafka for event processing.
- Swagger documentation for easy exploration of API endpoints.
- Uses Docker Compose for container orchestration.
- Built with Maven for easy dependency management.

## Requirements

- Java 17
- Maven
- Docker and Docker Compose

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd <repository-directory>
````

### Configure Environment Variables
Ensure that you have all necessary environment variables configured for your application. You can add them to a .env file or export them directly in your terminal.

## Running the Application
To run the application, use Docker Compose:

```bash
docker-compose up
```
This command will start all required services defined in the docker-compose.yml file.

## API Documentation
The API is documented using Swagger. Once the application is running, you can access the documentation at:

```bash
http://localhost:8080/webjars/swagger-ui/index.html
```

## Building the Project
To generate the build from OpenAPI and Avro definitions, run the following command:

```bash
mvn clean compile
```
This will compile the application and generate any required files.
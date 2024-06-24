# User Management Application

Application built using Java 17 and Spring Boot. It provides a RESTful API for managing users. The project utilizes Flyway for database migrations, PostgreSQL as the database, JUnit for testing, TestContainers for integration tests, Gradle for build automation, and Docker for containerization.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Application](#running-the-application)
    - [Running Tests](#running-tests)
- [API Endpoints](#api-endpoints)
- [Database Migrations](#database-migrations)
- [License](#license)

## Features

- Create, read, update, and delete (CRUD) user information.
- RESTful API endpoints.
- Database migrations with Flyway.
- Integration tests with TestContainers.
- Containerization with Docker.

## Technologies Used

- Java 17
- Spring Boot
- RESTful API
- Flyway
- PostgreSQL
- JUnit
- TestContainers
- Gradle
- Docker

## Getting Started

### Prerequisites

Ensure you have the following installed on your machine:

- Java 17
- Gradle
- Docker

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/DanSilva41/manager-user-api
   cd manager-user-api
   ```

### Running the Application

1. Build the project:

   ```sh
   ./gradlew build
   ```

2. Run the application:

   ```sh
   ./gradlew bootRun
   ```

### Running Tests

1. Run unit and integration tests:

   ```sh
   ./gradlew test
   ```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
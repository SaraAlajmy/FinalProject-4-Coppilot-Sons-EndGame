# ACL Final Project

## Overview

The project consists of several services, including a chat service, user service, notification service, group chat service, and an API gateway. It utilizes technologies like Java (Spring Boot likely, given `pom.xml` files), MongoDB, RabbitMQ, PostgreSQL, and Redis. The application can be run locally using Docker Compose or deployed to a Kubernetes cluster (Minikube for local development).

## Prerequisites

Before you begin, ensure you have the following installed:
*   Java (JDK)
*   Maven
*   Docker & Docker Compose
*   kubectl
*   Minikube (for Kubernetes deployment)

## Getting Started

### 1. Clone the Repository

```bash
git clone <your-repository-url>
```

### 2. Build the Application

All services are Maven-based. To build all services and create the necessary JAR files:

```bash
mvn clean install -DskipTests
mvn package -DskipTests
```
This command should be run at the root of the project where the main `pom.xml` is located, which will then build the sub-modules (services).

## Running the Project

You have two main options for running the application:

### Option 1: Using Docker Compose (for local development)

This method uses Docker Compose to build images for each service and run them along with backing services like MongoDB and RabbitMQ as defined in `docker-compose.yml` and service-specific compose files.

1.  **Start the application:**
    ```bash
    ./start.sh
    ```
    This script builds the JARs and starts the application using Docker Compose. It uses the `docker-compose.yml` file located in the E2E tests directory.
    
> **Note:** This will use MailHog for email testing instead of GMail. If you want to use GMail, you will need to override SPRING_MAIL properties in the `docker-compose.yml` file or with environment variables, or use kubernetes cluster as it is set up to use GMail.

### Option 2: Using Kubernetes (Minikube for local cluster)

Make sure you have Minikube running. You can start it with:

```bash
minikube start
```

Apply the Kubernetes manifests to your Minikube cluster:

```bash
kubectl apply -f k8s/
```

Get access to the gateway service:

```bash
minikube service apigateway-service --url
```

## Running Tests

### Local/Docker Compose Tests

The `test.sh` script is provided for running tests, end-to-end tests using the Docker Compose setup.

```bash
./test.sh
```

### Kubernetes Tests

The `k8s-test.sh` script is designed to run tests against a Kubernetes deployment.

```bash
./k8s-test.sh
```

>**Note:** This script requires a running Kubernetes cluster using Minikube. It will also start a `mailhog` container for email testing.

>**Note:** This runs against current manifests in `k8s/` and use `mailhog` for email testing (by applying the manifests in `e2e-tests/e2e-k8s/`), to run tests against a specific PR, use `./k8s-test.sh <pr_number>`.

This script:
1.  Applies Kubernetes manifests from `./k8s/`.
3.  Starts `mailhog` using Docker Compose.
4.  Applies Kubernetes manifests from `./e2e-tests/e2e-k8s/` to use the `mailhog` service instead of GMail
5.  Builds the `e2e-tests` module.
7.  Runs `mvn verify` with a `BASE_URL` pointing to the `api-gateway` service in the Kubernetes cluster.
8.  Stops the `mailhog` container.
9.  Re-applies the main Kubernetes manifests.

## Deployment Script (`deploy.sh`)

The `deploy.sh` script facilitates deployment to Kubernetes.
*   It takes a Pull Request number as an argument, which is used to tag Docker images (e.g., `pr-123`).
*   It applies these manifests to your Kubernetes cluster using `kubectl apply -f -` using images built from the PR.

**Usage:**
```bash
./deploy.sh <pr_number>
```

## Services

The project includes the following services:

*   **`api-gateway`**: (Located in `gateway/`) Entry point for client requests.
*   **`chat-service`**: (Located in `chat-service/`) Handles chat functionalities.
*   **`group-chat-service`**: (Located in `groupChatService/`) Manages group chat features.
*   **`notification-service`**: (Located in `notification-service/`) Responsible for sending notifications.
*   **`user-service`**: (Located in `userService/`) Manages user accounts and authentication.
*   **`e2e-tests`**: (Located in `e2e-tests/`) Contains end-to-end tests for the application.

**Datastores & Message Brokers:**
*   **MongoDB**: NoSQL database.
*   **RabbitMQ**: Message broker.
*   **PostgreSQL**: SQL database.
*   **Redis**: In-memory data store.

## Kubernetes Manifests

Kubernetes manifest files are located in the `k8s/` directory, with subdirectories for each service and component.
Additional test-specific Kubernetes manifests can be found in `e2e-tests/e2e-k8s/`.

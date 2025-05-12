# Microservices Chat Application

A modern, scalable chat application built using microservices architecture. This project implements a distributed system with multiple services working together to provide a robust chat platform with features like private messaging, group chats, and real-time notifications.

## Architecture

The application consists of the following microservices:

- **Gateway Service**: API Gateway that handles routing and request distribution
- **User Service**: Manages user authentication, profiles, and user-related operations
- **Chat Service**: Handles private messaging between users
- **Group Chat Service**: Manages group conversations and group-related operations
- **Notification Service**: Handles real-time notifications and message delivery
- **Shared**: Common utilities and shared code across services

## Technology Stack

- **Backend**: Java with Spring Boot
- **Database**: MongoDB
- **Message Broker**: RabbitMQ
- **API Gateway**: Spring Cloud Gateway
- **Containerization**: Docker
- **Orchestration**: Kubernetes (k8s)

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose
- MongoDB
- RabbitMQ

## Getting Started

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd FinalProject-4-Coppilot-Sons-EndGame
   ```

2. Start the required infrastructure using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   This will start:
   - MongoDB (port 27017)
   - MongoDB Express (port 8081)
   - RabbitMQ (ports 5672, 15672)

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run individual services:
   ```bash
   # Run Gateway Service
   cd gateway
   mvn spring-boot:run

   # Run User Service
   cd userService
   mvn spring-boot:run

   # Run Chat Service
   cd chat-service
   mvn spring-boot:run

   # Run Group Chat Service
   cd groupChatService
   mvn spring-boot:run

   # Run Notification Service
   cd notification-service
   mvn spring-boot:run
   ```

## Service Ports

- Gateway Service: 8080
- User Service: 8082
- Chat Service: 8083
- Group Chat Service: 8084
- Notification Service: 8085
- MongoDB: 27017
- MongoDB Express: 8081
- RabbitMQ: 5672 (AMQP), 15672 (Management UI)

## Features

- User authentication and authorization
- Private messaging between users
- Group chat functionality
- Real-time notifications
- Message persistence
- Scalable architecture

## Development

The project uses Maven for dependency management and build automation. Each service is a separate Maven module, making it easy to develop and maintain independently.

## Deployment

The project includes Kubernetes configuration files in the `k8s` directory for deploying the services to a Kubernetes cluster.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
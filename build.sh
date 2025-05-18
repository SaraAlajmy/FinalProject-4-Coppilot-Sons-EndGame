#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Define your Docker Hub username
DOCKERHUB_USERNAME="farahalfawzy"
IMAGE_TAG="latest"

echo "Building Maven projects..."
mvn clean install -DskipTests
mvn package -DskipTests

# Services and their directories
SERVICES=(
  "chat-service:./chat-service"
  "api-gateway-service:./gateway"
  "group-chat-service:./groupChatService"
  "notification-service:./notification-service"
  "user-service:./userService"
)

for service_info in "${SERVICES[@]}"; do
  IFS=':' read -r service_name service_dir <<< "$service_info"
  
  echo "Building and pushing $service_name..."
  docker build -t "${DOCKERHUB_USERNAME}/${service_name}:${IMAGE_TAG}" "${service_dir}"
done

echo "All images built and pushed successfully!"

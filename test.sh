#!/usr/bin/env bash

# Build jars
mvn clean install -DskipTests
mvn package -DskipTests

# Build the Docker images and start the containers
(cd e2e-tests && docker compose up -d --build)

# Wait for the containers to be up and running
sleep 10

# Run the tests
(cd e2e-tests && mvn verify)
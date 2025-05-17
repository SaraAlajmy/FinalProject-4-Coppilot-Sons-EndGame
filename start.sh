#!/usr/bin/env bash

# Build jars
mvn clean install -DskipTests
mvn package -DskipTests

# Build the Docker images and start the containers
(cd e2e-tests && docker compose up -d --build)

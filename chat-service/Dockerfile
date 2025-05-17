FROM openjdk:25-ea-4-jdk-oraclelinux9

# Add app jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar


# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-Dspring.cloud.compatibility-verifier.enabled=false","-jar", "app.jar"]
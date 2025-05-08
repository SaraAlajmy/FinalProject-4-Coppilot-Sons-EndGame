FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

COPY target/chat-service.jar chat-service.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar","chat-service.jar"]
FROM openjdk:17-slim
VOLUME /tmp
COPY target/api-gateway.jar app.jar
EXPOSE 8765
ENTRYPOINT ["java","-jar","/app.jar"]
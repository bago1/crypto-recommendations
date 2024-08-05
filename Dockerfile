FROM openjdk:17

WORKDIR /app

COPY target/crypto-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/prices /app/src/main/resources/prices

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker run -p 8080:8080 bago1/crypto:latest

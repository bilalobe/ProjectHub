# Stage 1: Build the application
FROM gradle:8.11-jdk23-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew build
RUN ls -la /app/build/libs

# Stage 2: Run the application
FROM openjdk:23-jdk
WORKDIR /app
COPY --from=build /app/build/libs/projecthub-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
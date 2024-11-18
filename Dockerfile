# Stage 1: Build the application
FROM openjdk:23-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

# Stage 2: Run the application
FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/projecthub-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
# Use an official OpenJDK runtime as a parent image
FROM openjdk:23-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build output
COPY build/libs/projecthub-0.1.0-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Define the entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
# Use the official Gradle image with Java 23 slim as the base image
FROM gradle:8.1.1-jdk23-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and project files
COPY . .

# Run the Gradle build to download dependencies
RUN ./gradlew build

# Set the default command to run the Gradle build
CMD ["./gradlew", "build"]
# Use the official Gradle image with Java 23 alpine as the base image
FROM gradle:8.11-jdk23-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and project files
COPY . .

# Run the Gradle build to download dependencies
RUN ./gradlew build

# Use an official PostgreSQL Alpine image as a parent image
FROM postgres:17-alpine

# Set the working directory
WORKDIR /home

# Install necessary packages as root
RUN apk update && apk add --no-cache \
    git \
    curl \
    sudo

# Create a non-root user and set permissions
ARG USERNAME=projecthub_devel
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN addgroup -g $USER_GID $USERNAME \
    && adduser -D -u $USER_UID -G $USERNAME -h /home/$USERNAME -s /bin/sh $USERNAME \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Set the default user
USER $USERNAME

# Copy the built application from the build image
COPY --from=build /app/build/libs/projecthub-0.1.0-SNAPSHOT.jar /app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app.jar"]
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

# Create a non-root user and set permissions
ARG USERNAME=projecthub_devel
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN addgroup -g $USER_GID $USERNAME \
    && adduser -u $USER_UID -G $USERNAME -h /home/$USERNAME -s /bin/sh -D $USERNAME \
    && apk update \
    && apk add --no-cache sudo \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Set the default user
USER $USERNAME

# Install any additional dependencies
RUN apk update && apk add --no-cache \
    git \
    curl \
    && rm -rf /var/cache/apk/*

# Use Docker secrets for sensitive data
COPY --from=build /app/build/libs/projecthub-0.1.0-SNAPSHOT.jar /app.jar

# Set environment variables for PostgreSQL
ENV POSTGRES_DB=${POSTGRES_DB}
ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app.jar"]
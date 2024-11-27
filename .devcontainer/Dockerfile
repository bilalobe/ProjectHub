# Description: Dockerfile for the development container
# Stage 1: Build the application
# FROM gradle:8.11-jdk23 AS build
# WORKDIR /app

# Copy gradle config files first to cache dependencies
# COPY app/build.gradle settings.gradle ./
# COPY gradlew ./
# COPY gradle/wrapper/ gradle/wrapper/

# Download dependencies
# RUN ./gradlew dependencies

# Copy source code
# COPY app/src/ ./src/

# Build the application
# RUN ./gradlew build --no-daemon

# Stage 2: Final image with PostgreSQL and Java
# FROM postgres:17-alpine

# Install necessary packages
# RUN apk update && apk add --no-cache \
#     sudo \
#     git \
#     curl \
#     shadow

# Create the user and set permissions
# ARG USERNAME=projecthub_devel
# ARG USER_UID=1000
# ARG USER_GID=1000

# RUN addgroup -g $USER_GID $USERNAME \
#     && adduser -D -u $USER_UID -G $USERNAME -h /home/$USERNAME -s /bin/sh $USERNAME \
#     && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
#     && chmod 0440 /etc/sudoers.d/$USERNAME

# Copy the built application from the build stage
# COPY --from=build /app/build/libs/app-0.1.0-SNAPSHOT.jar /app/app.jar
# RUN chown ${USERNAME}:${USERNAME} /app/app.jar

# Switch to non-root user
# USER $USERNAME

# Expose PostgreSQL and application ports
# EXPOSE 5432 8080

# Start both PostgreSQL and the Java application
# CMD ["sh", "-c", "sudo service postgresql start && java -jar /app/app.jar"]

# Stage 1: Build the application
FROM gradle:8.11-jdk23 AS build
WORKDIR /app

# Copy gradle config files first to cache dependencies
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle/wrapper/ gradle/wrapper/

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ ./src/

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Create the final image
FROM openjdk:23-jdk-slim
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Entry point for the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
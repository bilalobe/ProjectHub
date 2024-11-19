# Stage 1: Build the application
FROM gradle:8.11-jdk23 AS build
WORKDIR /app

# Copy gradle config files first to cache dependencies
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle/wrapper/ gradle/wrapper/

# Download dependencies
RUN ./gradlew dependencies

# Copy source code
COPY . .

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Final image with PostgreSQL and Java
FROM postgres:17-alpine

# Install OpenJDK and necessary packages
RUN apk update && apk add --no-cache \
    apt-utils \
    openjdk23 \
    git \
    curl \
    sudo

# Create a non-root user
ARG USERNAME=projecthub_devel
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN addgroup -g $USER_GID $USERNAME \
    && adduser -D -u $USER_UID -G $USERNAME -h /home/$USERNAME -s /bin/sh $USERNAME \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Copy the built application from the build stage
COPY --from=build /app/build/libs/app-0.1.0-SNAPSHOT.jar /app/app.jar
RUN chown ${USERNAME}:${USERNAME} /app/app.jar

# Switch to non-root user
USER $USERNAME

# Expose PostgreSQL and application ports
EXPOSE 5432 8080

# Start both PostgreSQL and the Java application
CMD ["sh", "-c", "sudo service postgresql start && java -jar /app/app.jar"]
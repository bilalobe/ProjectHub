# Root Dockerfile for the projecthub application
# Stage 1: Build the application
FROM gradle:8.11-jdk23 AS build
WORKDIR /app

# Copy gradle config files first to cache dependencies
COPY app/build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle/wrapper/ gradle/wrapper/

# Download dependencies
RUN ./gradlew dependencies

# Copy source code
COPY app/src/ ./src/

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Run the application
FROM openjdk:23-jdk
WORKDIR /app

# Create the sudoers.d directory if it does not exist
RUN mkdir -p /etc/sudoers.d

# Create a non-root user
ARG USERNAME=projecthub_devel
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN groupadd --gid $USER_GID $USERNAME || echo "Group exists" \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && echo "$USERNAME ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Set the non-root user and working directory
USER $USERNAME
WORKDIR /app 

# Copy jar from build stage
COPY --from=build /app/build/libs/app-0.1.0-SNAPSHOT.jar /app.jar

# Expose the application port and set the entry point
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
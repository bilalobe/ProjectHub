# Stage 1: Build the application
FROM gradle:8.11-jdk23 AS build

# Set working directory
WORKDIR /app

# Copy gradle files for caching
COPY build.gradle settings.gradle ./ 
COPY gradle/ gradle/
COPY gradlew ./ 

# Download dependencies
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY . .

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Runtime
FROM openjdk:23-jdk

# Create non-root user
ARG USERNAME=projecthub_devel
ARG USER_UID=1001
ARG USER_GID=$USER_UID

RUN groupadd --gid $USER_GID $USERNAME && \
    useradd --uid $USER_UID --gid $USER_GID -m $USERNAME

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown ${USERNAME}:${USERNAME} /app/app.jar

# Use non-root user
USER $USERNAME

# Expose application port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
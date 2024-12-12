# Dockerfile

# Use the official Java Dev Container image as a base
FROM mcr.microsoft.com/devcontainers/java:21-bookworm

# Install Node.js (LTS version) and Angular CLI
RUN curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y nodejs \
    && npm install -g @angular/cli

# Install PostgreSQL
RUN apt-get update && apt-get install -y postgresql postgresql-contrib

# Set environment variables for PostgreSQL
ENV POSTGRES_DB=projecthub
ENV POSTGRES_USER=projecthub
ENV POSTGRES_PASSWORD=projecthub

# Expose necessary ports
EXPOSE 8080 4200 5432

# Set the default command
CMD ["sleep", "infinity"]
# Use the official Java image as a parent image
FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

# Install Node.js
RUN curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get install -y nodejs

# Install PostgreSQL
RUN apt-get update && apt-get install -y postgresql-14 postgresql-client-14

# Install Angular CLI
RUN npm install -g @angular/cli

# Set environment variables for PostgreSQL
ENV POSTGRES_DB=projecthub
ENV POSTGRES_USER=projecthub
ENV POSTGRES_PASSWORD=projecthub

# Expose ports
EXPOSE 8080 5432 4200

# Set the default command to run when starting the container
CMD ["bash"]
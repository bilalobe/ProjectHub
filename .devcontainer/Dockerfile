FROM mcr.microsoft.com/devcontainers/java:21-bookworm

# Install Node.js (LTS version) and Angular CLI
RUN curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y nodejs postgresql postgresql-contrib \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && npm install -g @angular/cli

# Set environment variables for PostgreSQL
ENV POSTGRES_DB=projecthub
ENV POSTGRES_USER=projecthub
ENV POSTGRES_PASSWORD=projecthub

# Expose necessary ports
EXPOSE 8080 4200 5432

# Set the default command
CMD ["sleep", "infinity"]
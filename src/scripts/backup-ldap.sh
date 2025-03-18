#!/bin/bash

# LDAP backup script for ProjectHub
# This script creates a backup of the ApacheDS LDAP directory

# Configuration
BACKUP_DIR="/var/backups/projecthub/ldap"
INSTANCE_DIR="/var/lib/apacheds-2.0.0"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER_NAME="projecthub-apacheds"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Stop ApacheDS container to ensure data consistency
echo "Stopping ApacheDS container..."
docker stop "$CONTAINER_NAME"

# Create backup
echo "Creating LDAP backup..."
tar -czf "$BACKUP_DIR/ldap_backup_$DATE.tar.gz" "$INSTANCE_DIR"

# Restart ApacheDS container
echo "Restarting ApacheDS container..."
docker start "$CONTAINER_NAME"

# Cleanup old backups (keep last 7 days)
find "$BACKUP_DIR" -name "ldap_backup_*.tar.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/ldap_backup_$DATE.tar.gz"
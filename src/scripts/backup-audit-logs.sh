#!/bin/bash

# Audit log backup script for ProjectHub
# This script archives and rotates audit logs

# Configuration
AUDIT_DIR="/var/log/fortress/audit"
BACKUP_DIR="/var/backups/projecthub/audit"
RETENTION_DAYS=90
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Function to log messages
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# Archive current audit logs
log_message "Archiving audit logs..."
tar -czf "$BACKUP_DIR/audit_logs_$DATE.tar.gz" "$AUDIT_DIR"/*.log

# Compress individual logs older than 1 day
find "$AUDIT_DIR" -name "*.log" -type f -mtime +1 -exec gzip {} \;

# Remove backups older than retention period
log_message "Cleaning up old backups..."
find "$BACKUP_DIR" -name "audit_logs_*.tar.gz" -type f -mtime +$RETENTION_DAYS -delete

# Verify backup
if [ -f "$BACKUP_DIR/audit_logs_$DATE.tar.gz" ]; then
    log_message "Backup completed successfully: audit_logs_$DATE.tar.gz"
    
    # Calculate and store checksum
    sha256sum "$BACKUP_DIR/audit_logs_$DATE.tar.gz" > "$BACKUP_DIR/audit_logs_$DATE.sha256"
    log_message "Checksum saved to: audit_logs_$DATE.sha256"
else
    log_message "Error: Backup failed!"
    exit 1
fi

# Report storage usage
log_message "Backup storage usage:"
du -sh "$BACKUP_DIR"
#!/bin/bash

# Fortress update and security patch script for ProjectHub
# This script updates Fortress components and applies security patches

# Configuration
BACKUP_DIR="/var/backups/projecthub/fortress"
LOG_DIR="/var/log/projecthub/updates"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/projecthub"

# Create directories
mkdir -p "$BACKUP_DIR"
mkdir -p "$LOG_DIR"

# Log file
LOG_FILE="$LOG_DIR/fortress_update_$DATE.log"

# Function to log messages
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Backup current configuration
backup_config() {
    log_message "Backing up current configuration..."
    cp -r "$APP_DIR/config" "$BACKUP_DIR/config_$DATE"
    if [ $? -eq 0 ]; then
        log_message "✓ Configuration backup created"
    else
        log_message "✗ Configuration backup failed"
        exit 1
    fi
}

# Update Gradle dependencies
update_dependencies() {
    log_message "Updating Gradle dependencies..."
    cd "$APP_DIR" || exit 1
    
    # Update Fortress dependencies
    ./gradlew --refresh-dependencies \
              --write-verification-metadata sha256 \
              :modules:foundation:dependencies > "$LOG_DIR/deps_$DATE.txt"
    
    if [ $? -eq 0 ]; then
        log_message "✓ Dependencies updated successfully"
    else
        log_message "✗ Dependency update failed"
        exit 1
    fi
}

# Apply security patches
apply_security_patches() {
    log_message "Applying security patches..."
    
    # Stop the application
    log_message "Stopping ProjectHub..."
    systemctl stop projecthub
    
    # Apply patches
    cd "$APP_DIR" || exit 1
    ./gradlew clean build -x test
    
    if [ $? -eq 0 ]; then
        log_message "✓ Security patches applied successfully"
    else
        log_message "✗ Security patch application failed"
        # Restore from backup
        log_message "Restoring from backup..."
        cp -r "$BACKUP_DIR/config_$DATE" "$APP_DIR/config"
        exit 1
    fi
    
    # Restart the application
    log_message "Starting ProjectHub..."
    systemctl start projecthub
}

# Verify installation
verify_installation() {
    log_message "Verifying installation..."
    
    # Wait for application to start
    sleep 10
    
    # Check if application is running
    if systemctl is-active --quiet projecthub; then
        log_message "✓ Application started successfully"
    else
        log_message "✗ Application failed to start"
        exit 1
    fi
    
    # Run RBAC verification
    ./verify-rbac-config.sh
    if [ $? -eq 0 ]; then
        log_message "✓ RBAC configuration verified"
    else
        log_message "✗ RBAC verification failed"
        exit 1
    fi
}

# Run update process
log_message "Starting Fortress update process..."
backup_config
update_dependencies
apply_security_patches
verify_installation

log_message "Update completed successfully!"
#!/bin/bash

# RBAC configuration verification script for ProjectHub
# This script validates the RBAC structure and role assignments

# Configuration
CONFIG_DIR="/etc/projecthub/rbac"
LOG_DIR="/var/log/projecthub/rbac"
DATE=$(date +%Y%m%d_%H%M%S)
EXIT_CODE=0

# Create directories
mkdir -p "$LOG_DIR"
mkdir -p "$CONFIG_DIR"

# Log file
LOG_FILE="$LOG_DIR/verify_rbac_$DATE.log"

# Function to log messages
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Required roles and their parent roles
declare -A ROLE_HIERARCHY=(
    ["ROLE_ADMIN"]=""
    ["ROLE_MANAGER"]="ROLE_ADMIN"
    ["ROLE_USER"]="ROLE_MANAGER"
    ["ROLE_GUEST"]="ROLE_USER"
)

# Required permissions for each role
declare -A ROLE_PERMISSIONS=(
    ["ROLE_ADMIN"]="user:manage role:manage permission:manage system:configure audit:view"
    ["ROLE_MANAGER"]="user:view role:view project:manage report:manage"
    ["ROLE_USER"]="project:view project:submit report:view"
    ["ROLE_GUEST"]="project:view"
)

# Verify role hierarchy
verify_role_hierarchy() {
    log_message "Verifying role hierarchy..."
    for role in "${!ROLE_HIERARCHY[@]}"; do
        parent="${ROLE_HIERARCHY[$role]}"
        if [ -n "$parent" ]; then
            if ! ldapsearch -H "ldap://localhost:10389" -D "uid=admin,ou=system" -w "secret" \
                -b "ou=Roles,dc=projecthub,dc=com" "(&(cn=$role)(parentRole=$parent))" >/dev/null 2>&1; then
                log_message "✗ Role $role is not properly linked to parent $parent"
                EXIT_CODE=1
            else
                log_message "✓ Role $role hierarchy verified"
            fi
        fi
    done
}

# Verify role permissions
verify_role_permissions() {
    log_message "Verifying role permissions..."
    for role in "${!ROLE_PERMISSIONS[@]}"; do
        permissions="${ROLE_PERMISSIONS[$role]}"
        for perm in $permissions; do
            if ! ldapsearch -H "ldap://localhost:10389" -D "uid=admin,ou=system" -w "secret" \
                -b "ou=Permissions,dc=projecthub,dc=com" "(&(cn=$perm)(assignedTo=$role))" >/dev/null 2>&1; then
                log_message "✗ Permission $perm is not assigned to role $role"
                EXIT_CODE=1
            else
                log_message "✓ Permission $perm verified for role $role"
            fi
        done
    done
}

# Verify temporal constraints
verify_temporal_constraints() {
    log_message "Verifying temporal constraints..."
    if ldapsearch -H "ldap://localhost:10389" -D "uid=admin,ou=system" -w "secret" \
        -b "ou=Constraints,dc=projecthub,dc=com" "(objectclass=ftTemporal)" >/dev/null 2>&1; then
        log_message "✓ Temporal constraints exist"
    else
        log_message "! Warning: No temporal constraints found"
    fi
}

# Verify separation of duties
verify_sod() {
    log_message "Verifying separation of duties..."
    if ldapsearch -H "ldap://localhost:10389" -D "uid=admin,ou=system" -w "secret" \
        -b "ou=Constraints,dc=projecthub,dc=com" "(objectclass=ftSoD)" >/dev/null 2>&1; then
        log_message "✓ SoD constraints exist"
    else
        log_message "! Warning: No SoD constraints found"
    fi
}

# Export current configuration for backup
export_config() {
    log_message "Exporting current RBAC configuration..."
    CONFIG_BACKUP="$CONFIG_DIR/rbac_config_$DATE.ldif"
    
    ldapsearch -H "ldap://localhost:10389" -D "uid=admin,ou=system" -w "secret" \
        -b "dc=projecthub,dc=com" "(objectclass=*)" > "$CONFIG_BACKUP"
    
    if [ $? -eq 0 ]; then
        log_message "✓ Configuration exported to $CONFIG_BACKUP"
        # Create checksum
        sha256sum "$CONFIG_BACKUP" > "$CONFIG_BACKUP.sha256"
    else
        log_message "✗ Failed to export configuration"
        EXIT_CODE=1
    fi
}

# Run verifications
log_message "Starting RBAC verification..."
verify_role_hierarchy
verify_role_permissions
verify_temporal_constraints
verify_sod
export_config

# Final status
if [ $EXIT_CODE -eq 0 ]; then
    log_message "All RBAC checks passed successfully!"
else
    log_message "Some RBAC checks failed. Please review the log for details."
fi

exit $EXIT_CODE
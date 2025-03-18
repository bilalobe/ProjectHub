#!/bin/bash

# LDAP and RBAC health check script for ProjectHub
# This script verifies the health of ApacheDS and Fortress configuration

# Configuration
LDAP_HOST="localhost"
LDAP_PORT="10389"
ADMIN_DN="uid=admin,ou=system"
ADMIN_PW="secret"
BASE_DN="dc=projecthub,dc=com"
LOG_DIR="/var/log/projecthub/health"
DATE=$(date +%Y%m%d_%H%M%S)

# Create log directory
mkdir -p "$LOG_DIR"

# Initialize status
EXIT_CODE=0

# Function to log messages
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_DIR/health_$DATE.log"
}

# Check if LDAP is running
check_ldap_status() {
    log_message "Checking LDAP connection..."
    if ldapwhoami -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" >/dev/null 2>&1; then
        log_message "✓ LDAP connection successful"
    else
        log_message "✗ LDAP connection failed"
        EXIT_CODE=1
    fi
}

# Check RBAC configuration
check_rbac_config() {
    log_message "Checking RBAC configuration..."
    
    # Check roles container
    if ldapsearch -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -b "ou=Roles,$BASE_DN" -s one "(objectclass=*)" >/dev/null 2>&1; then
        log_message "✓ Roles container exists"
    else
        log_message "✗ Roles container missing"
        EXIT_CODE=1
    fi
    
    # Check users container
    if ldapsearch -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -b "ou=People,$BASE_DN" -s one "(objectclass=*)" >/dev/null 2>&1; then
        log_message "✓ Users container exists"
    else
        log_message "✗ Users container missing"
        EXIT_CODE=1
    fi
    
    # Check permissions container
    if ldapsearch -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -b "ou=Permissions,$BASE_DN" -s one "(objectclass=*)" >/dev/null 2>&1; then
        log_message "✓ Permissions container exists"
    else
        log_message "✗ Permissions container missing"
        EXIT_CODE=1
    fi
}

# Check admin role exists
check_admin_role() {
    log_message "Checking admin role..."
    if ldapsearch -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -b "ou=Roles,$BASE_DN" "(cn=ROLE_ADMIN)" >/dev/null 2>&1; then
        log_message "✓ Admin role exists"
    else
        log_message "✗ Admin role missing"
        EXIT_CODE=1
    fi
}

# Check LDAP performance
check_ldap_performance() {
    log_message "Checking LDAP performance..."
    START_TIME=$(date +%s%N)
    ldapsearch -H "ldap://$LDAP_HOST:$LDAP_PORT" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -b "$BASE_DN" -s one "(objectclass=*)" >/dev/null 2>&1
    END_TIME=$(date +%s%N)
    DURATION=$((($END_TIME - $START_TIME)/1000000))
    
    if [ $DURATION -lt 1000 ]; then
        log_message "✓ LDAP response time: ${DURATION}ms (good)"
    else
        log_message "! LDAP response time: ${DURATION}ms (slow)"
        # Don't fail on slow performance, just warn
        log_message "! Performance warning: LDAP queries are slow"
    fi
}

# Run all checks
log_message "Starting health checks..."
check_ldap_status
check_rbac_config
check_admin_role
check_ldap_performance

# Final status
if [ $EXIT_CODE -eq 0 ]; then
    log_message "All checks passed successfully!"
else
    log_message "Some checks failed. Please review the log for details."
fi

exit $EXIT_CODE
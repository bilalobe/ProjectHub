# ProjectHub Utility Scripts

Collection of maintenance and operational scripts for ProjectHub deployment and management.

## Available Scripts

### Backup Scripts

- `backup-audit-logs.sh`: Backup security audit logs
- `backup-ldap.sh`: Backup Apache Fortress LDAP data

### Health Check Scripts

- `check-ldap-health.sh`: Verify LDAP server health
- `verify-rbac-config.sh`: Validate RBAC configuration

### Maintenance Scripts

- `update-fortress.sh`: Update Apache Fortress configuration

## Usage Guide

### Backup Operations

1. Audit Log Backup:
   ```bash
   ./backup-audit-logs.sh [--retention-days=30]
   ```

2. LDAP Data Backup:
   ```bash
   ./backup-ldap.sh [--target-dir=/backups]
   ```

### Health Checks

1. LDAP Health Check:
   ```bash
   ./check-ldap-health.sh [--timeout=30]
   ```

2. RBAC Verification:
   ```bash
   ./verify-rbac-config.sh [--config-file=rbac.yaml]
   ```

### System Updates

1. Fortress Update:
   ```bash
   ./update-fortress.sh [--version=latest]
   ```

## Configuration

Scripts read configuration from environment variables:

```bash
# Common Settings
export PH_ENV=production              # Environment (development/staging/production)
export PH_BACKUP_DIR=/var/backups    # Backup storage location
export PH_LOG_LEVEL=info             # Logging level

# LDAP Settings
export LDAP_HOST=localhost
export LDAP_PORT=10389
export LDAP_ADMIN_USER=admin
export LDAP_ADMIN_PASS=secret

# Backup Settings
export BACKUP_RETENTION_DAYS=30
export BACKUP_COMPRESS=true
```

## Logging

All scripts use standardized logging:

- INFO level for normal operations
- WARN level for potential issues
- ERROR level for failures
- DEBUG level for troubleshooting

Logs are written to:

- Console output
- System log (/var/log/projecthub/)
- Audit log for security-related operations

## Error Handling

Scripts follow these error handling practices:

- Exit codes for different error conditions
- Descriptive error messages
- Cleanup on failure
- Logging of all failures

## Security

- Scripts run with minimum required privileges
- Sensitive data is read from environment variables
- Temporary files are securely handled
- Audit logging for security operations

## Scheduling

Recommended cron schedules:

```bash
# Daily backups at 2 AM
0 2 * * * /opt/projecthub/scripts/backup-audit-logs.sh

# Hourly health checks
0 * * * * /opt/projecthub/scripts/check-ldap-health.sh

# Weekly RBAC verification
0 0 * * 0 /opt/projecthub/scripts/verify-rbac-config.sh
```

## Troubleshooting

1. Script Permissions:
   ```bash
   chmod +x /opt/projecthub/scripts/*.sh
   ```

2. Environment Variables:
   ```bash
   source /etc/projecthub/env.sh
   ```

3. Log Analysis:
   ```bash
   tail -f /var/log/projecthub/scripts.log
   ```

## Contributing

When adding new scripts:

1. Follow the established naming convention
2. Include proper error handling
3. Add logging statements
4. Update this documentation
5. Add appropriate tests

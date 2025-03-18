# Security Changelog

All notable security-related changes to ProjectHub are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2024-01-20

### Added
- Apache Fortress integration for RBAC
- WebAuthn/passkey support
- Comprehensive security documentation
- Security incident response procedures
- SOC 2 compliance preparations
- Security monitoring and metrics
- Audit logging system

### Changed
- Enhanced password policies with Argon2id
- Improved LDAP connection security
- Updated security dependencies
- Strengthened access control checks

### Security
- Fixed CVE-2023-XXXX: LDAP injection vulnerability
- Fixed CVE-2023-YYYY: Authentication bypass in WebAuthn flow
- Patched Apache Directory Server vulnerabilities
- Updated cryptographic libraries

## [1.1.0] - 2023-12-15

### Added
- Basic role-based access control
- Initial security documentation
- Password complexity requirements
- Session management improvements

### Security
- Fixed CVE-2023-ZZZZ: Session fixation vulnerability
- Updated Spring Security dependencies
- Enhanced input validation

## [1.0.0] - 2023-11-01

### Added
- Basic authentication system
- Simple role management
- Password hashing with bcrypt
- Initial security configurations

## Security Advisories

### [SEC-001] - 2024-01-15
- **Severity**: High
- **Description**: LDAP injection vulnerability in user search
- **Affected Versions**: < 2.0.0
- **Solution**: Upgrade to 2.0.0 or apply patch
- **CVE**: CVE-2023-XXXX

### [SEC-002] - 2024-01-10
- **Severity**: Medium
- **Description**: WebAuthn authentication bypass
- **Affected Versions**: < 2.0.0
- **Solution**: Upgrade to 2.0.0
- **CVE**: CVE-2023-YYYY
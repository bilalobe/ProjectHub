package com.projecthub.auth.service;

/**
 * Service for password operations like hashing, verification, and validation.
 */
public interface PasswordService {
    /**
     * Hashes a raw password for secure storage.
     *
     * @param rawPassword The plain text password to hash
     * @return A secure hash of the password
     */
    String hash(String rawPassword);

    /**
     * Verifies if a raw password matches a hashed password.
     *
     * @param rawPassword The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    boolean matches(String rawPassword, String hashedPassword);

    /**
     * Determines if a password needs to be rehashed (e.g., if hash algorithm updated).
     *
     * @param hashedPassword The currently stored password hash
     * @return true if rehashing is recommended, false otherwise
     */
    boolean needsRehash(String hashedPassword);
}
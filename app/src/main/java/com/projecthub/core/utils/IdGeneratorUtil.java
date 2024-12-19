package com.projecthub.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdGeneratorUtil {

    // Private constructor to prevent instantiation
    private IdGeneratorUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a unique ID based on input data.
     *
     * @param data The input string to generate ID from
     * @return A numeric ID derived from the hash of the input
     */
    public static int generateUniqueId(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            String hexString = bytesToHex(digest);
            String idString = hexString.substring(0, Math.min(7, hexString.length()));
            return Integer.parseInt(idString, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new IdGenerationException("SHA-256 algorithm not available.", e);
        } catch (NumberFormatException e) {
            throw new IdGenerationException("Failed to parse hash to integer.", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        if (hash == null || hash.length == 0) {
            throw new IllegalArgumentException("Hash byte array cannot be null or empty.");
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Custom exception for ID generation errors
    public static class IdGenerationException extends RuntimeException {
        public IdGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
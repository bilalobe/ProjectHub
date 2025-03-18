package com.projecthub.base.shared.utils;

import java.nio.charset.StandardCharsets;
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
    public static int generateUniqueId(final String data) {
        if (null == data || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty.");
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes(StandardCharsets.UTF_8));
            final byte[] digest = md.digest();
            final String hexString = IdGeneratorUtil.bytesToHex(digest);
            final String idString = hexString.substring(0, Math.min(7, hexString.length()));
            return Integer.parseInt(idString, 16);
        } catch (final NoSuchAlgorithmException e) {
            throw new IdGenerationException("SHA-256 algorithm not available.", e);
        } catch (final NumberFormatException e) {
            throw new IdGenerationException("Failed to parse hash to integer.", e);
        }
    }

    private static String bytesToHex(final byte[] hash) {
        if (null == hash || 0 == hash.length) {
            throw new IllegalArgumentException("Hash byte array cannot be null or empty.");
        }
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : hash) {
            final String hex = Integer.toHexString(0xff & (int) b);
            if (1 == hex.length()) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Custom exception for ID generation errors
    public static class IdGenerationException extends RuntimeException {
        public IdGenerationException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}

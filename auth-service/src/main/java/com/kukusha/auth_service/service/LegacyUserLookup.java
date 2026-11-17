package com.kukusha.auth_service.service;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * DEMO ONLY: deliberately contains issues for Sonar to detect. Not wired into the application.
 */
public class LegacyUserLookup {
    // Hardcoded credential (java:S2068)
    private static final String DB_PASSWORD = "admin123";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/products_management";

    // SQL injection + resources never closed (java:S3649, java:S2095)
    public String findEmail(String username) throws Exception {
        Connection connection = DriverManager.getConnection(DB_URL, "usr", DB_PASSWORD);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT email FROM users WHERE username = '" + username + "'");

        if (resultSet.next()) {
            return resultSet.getString("email");
        }
        return null;
    }

    // Weak hash algorithm (java:S4790)
    public byte[] hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(password.getBytes());
    }

    // Predictable random for a security token (java:S2245)
    public String generateResetToken() {
        Random random = new Random();
        return Long.toHexString(random.nextLong());
    }

    // Optional.get() without check, possible null dereference (java:S3655, java:S2259)
    public int emailLength(Optional<String> email, Map<String, String> cache) {
        String cached = cache.get("email");
        System.out.println("Looking up email length");
        return email.get().length() + cached.length();
    }

    // Empty catch block, unused variable (java:S108, java:S1481)
    public void audit(String username) {
        int attempts = 0;
        try {
            findEmail(username);
        } catch (Exception e) {
        }
    }

    // Repeated literals (java:S1192) and high cognitive complexity (java:S3776)
    public String classify(String role, int age, boolean active, boolean locked) {
        if (active) {
            if (!locked) {
                if (role.equals("ADMIN")) {
                    if (age > 18) {
                        for (int i = 0; i < age; i++) {
                            if (i % 2 == 0) {
                                if (role.equals("ADMIN") && age > 30) {
                                    return "ADMIN-SENIOR";
                                } else if (role.equals("ADMIN")) {
                                    return "ADMIN-JUNIOR";
                                }
                            }
                        }
                    }
                } else if (role.equals("USER")) {
                    return "USER-ACTIVE";
                }
            } else {
                return "USER-LOCKED";
            }
        }
        return "USER-INACTIVE";
    }

    // Unused private method (java:S1144)
    private void unusedHelper() {
    }
}

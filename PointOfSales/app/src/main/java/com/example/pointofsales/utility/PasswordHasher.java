package com.example.pointofsales.utility;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PasswordHasher utility class to generate a salt and hash the password for security purposes when storing in the database
 */
public class PasswordHasher {

    private static final int ITERATIONS = 1024;
    private static final int KEY_LENGTH = 128;

    /**
     * Generate a String of random salt of size 64
     * @return
     */
    public static String generateRandomSalt() {

        byte[] salt = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    /**
     * Hash the password alongside the given salt
     * @param password
     * @param salt
     * @return
     */
    public static String hash(String password, String salt) {
        try {

            // Get the PBKDF2WithHmacSHA1 algorithm in the hash generation
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), Base64.decode(salt, Base64.DEFAULT), ITERATIONS, KEY_LENGTH);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            // Return the generated hash
            return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }
}

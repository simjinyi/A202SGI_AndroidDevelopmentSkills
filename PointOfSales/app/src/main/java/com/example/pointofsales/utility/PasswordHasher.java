package com.example.pointofsales.utility;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int ITERATIONS = 1024;
    private static final int KEY_LENGTH = 128;

    public static String generateRandomSalt() {

        byte[] salt = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    public static String hash(String password, String salt) {
        try {

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), Base64.decode(salt, Base64.DEFAULT), ITERATIONS, KEY_LENGTH);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }
}

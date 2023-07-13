package com.narsil.misc;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * random generator toolkit
 *
 * @author iamnarsil
 * @version 20230712
 * @since 20230328
 */
public class RandUtils {

    private static final Logger LOGGER = Logger.getLogger("RandUtils");
    private static RandUtils instance = null;

    // For secure random
    private SecureRandom secureRandom;

    public static RandUtils getInstance() {

        synchronized (RandUtils.class) {
            if (instance == null) {
                instance = new RandUtils();
            }
        }

        return instance;
    }

    private RandUtils() {

        try {
            // initialize secure random (can improve performance)
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.nextInt(10);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public String generateNumber(int length) {

        // number (0-9) => 10 characters
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(secureRandom.nextInt(10));
        }

        return result.toString();
    }

    public String generateString(int length) {

        // number (0-9) + alphabet (A-Z) => 36 characters
        final String sample = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(sample.charAt(secureRandom.nextInt(36)));
        }

        return result.toString();
    }

    public String generateHexString(int length) {

        // number (0-9) + alphabet (A-F) => 16 characters
        final String sample = "0123456789ABCDEF";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(sample.charAt(secureRandom.nextInt(16)));
        }

        return result.toString();
    }

    public byte[] generateBytes(int length) {

        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            result[i] = (byte)(secureRandom.nextInt(256) & (byte) 0xFF);
        }

        return result;
    }

    public int generateInteger(int bound) {
        return secureRandom.nextInt(bound);
    }
}

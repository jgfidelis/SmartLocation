
package com.zhji.location.app.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class Utils {
    /**
     * This method generate MD5 hash
     * 
     * @param message
     * @return
     */
    public static String generateHash(final String message) {
        String digest = null;
        MessageDigest md;
        try {

            md = MessageDigest.getInstance("MD5");

            final byte[] hash = md.digest(message.getBytes("UTF-8"));

            // converting byte array to Hexadecimal String
            final StringBuilder sb = new StringBuilder(2 * hash.length);
            for (final byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();

            return digest;
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String generateLocationID(final double latitude, final double longitude) {
        final DecimalFormat df = new DecimalFormat("##.####");
        return generateHash(df.format(latitude) + df.format(longitude));
    }
}

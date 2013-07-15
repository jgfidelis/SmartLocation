
package com.zhji.location.app.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.zhji.location.app.R;
import com.zhji.location.app.SmartLocationApp;

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

    /**
     * Convenience method to show notification
     * 
     * @param title
     * @param text
     */
    public static void showNotification(final String title, final String text, final int id) {
        // Build notification
        final Context context = SmartLocationApp.getContext();
        final Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher).build();

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, notification);
    }
}

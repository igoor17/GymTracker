package es.eduardo.gymtracker.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import es.eduardo.gymtracker.R;

/**
 * BroadcastReceiver to handle alarms and display weight reminder notifications.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "notifyWeight";

    /**
     * Receives the broadcast intent containing the alarm and handles displaying notifications.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The intent containing information about the broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationPref", Context.MODE_PRIVATE);
        boolean isNotificationOn = sharedPreferences.getBoolean("isNotificationOn", false); // Default is false

        // Check if notification is enabled
        if (isNotificationOn) {
            // Check if the app has the necessary permission to post notifications
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // If permission is not granted, request the permission
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
                return;
            }
            // Create notification channel (required for Android Oreo and above)
            createNotificationChannel(context);

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("GymTracker")
                    .setContentText("It's time to weigh yourself and update your weight in the app.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Display the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        }
    }

    /**
     * Creates a notification channel for weight reminders (required for Android Oreo and above).
     *
     * @param context The context in which the notification channel is created.
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weight Reminder";
            String description = "Channel for weight reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

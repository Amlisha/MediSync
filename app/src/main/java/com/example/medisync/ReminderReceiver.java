
package com.example.medisync;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // ==========================================
        // CHECK REMINDER OWNERSHIP
        // ==========================================

        // Get the user ID saved when the alarm was scheduled
        int reminderUserId = intent.getIntExtra(
                "user_id", -1
        );

        // Do not display reminders without a valid owner
        if (reminderUserId <= 0) {
            return;
        }

        // Get the currently logged-in username
        SharedPreferences accountPreferences =
                context.getSharedPreferences(
                        "MediSyncAccount",
                        Context.MODE_PRIVATE
                );

        String username = accountPreferences.getString(
                "username", ""
        );

        // No logged-in user
        if (username.isEmpty()) {
            return;
        }

        // Find the logged-in user's ID
        DatabaseHelper dbHelper =
                new DatabaseHelper(context);

        int currentUserId = dbHelper.getUserId(username);

        // Do not show another user's reminder
        if (currentUserId <= 0 ||
                currentUserId != reminderUserId) {
            return;
        }

        // ==========================================
        // GET REMINDER DETAILS
        // ==========================================

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        if (title == null) {
            title = "MediSync Reminder";
        }

        if (message == null) {
            message = "You have a scheduled reminder.";
        }

        String channelId = "medisync_reminders";

        // ==========================================
        // CREATE NOTIFICATION CHANNEL
        // ==========================================

        // Android 8.0 and above require a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "MediSync Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Medicine and appointment reminders"
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // ==========================================
        // BUILD NOTIFICATION
        // ==========================================

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(
                        context,
                        channelId
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true);

        // ==========================================
        // CHECK NOTIFICATION PERMISSION
        // ==========================================

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        // ==========================================
        // DISPLAY NOTIFICATION
        // ==========================================

        NotificationManagerCompat.from(context)
                .notify(
                        (int) System.currentTimeMillis(),
                        notification.build()
                );
    }
}

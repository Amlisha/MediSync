
package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RemindScheduler {

    // ==========================================
    // GET LOGGED-IN USER ID
    // ==========================================

    private static int getCurrentUserId(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "MediSyncAccount",
                        Context.MODE_PRIVATE
                );

        String username = preferences.getString(
                "username", ""
        );

        if (username.isEmpty()) {
            return -1;
        }

        DatabaseHelper dbHelper =
                new DatabaseHelper(context);

        return dbHelper.getUserId(username);
    }

    // ==========================================
    // MEDICINE REMINDER REQUEST CODE
    // ==========================================

    // Each user gets a different negative request code.
    // Appointment IDs are positive, so they won't collide.
    public static int getMedicineRequestCode(int userId) {

        if (userId <= 0) {
            return 0;
        }

        return -userId;
    }

    // ==========================================
    // SCHEDULE REMINDER
    // ==========================================

    public static boolean scheduleReminder(
            Context context,
            String date,
            String time,
            String title,
            String message,
            int requestCode) {

        // Find the currently logged-in user
        int userId = getCurrentUserId(context);

        // Never schedule a reminder without a valid user
        if (userId <= 0) {
            return false;
        }

        try {

            Date reminderDate;

            // First try 24-hour time, for example 20:45
            SimpleDateFormat dateTime24 =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.US
                    );

            dateTime24.setLenient(false);

            try {

                reminderDate = dateTime24.parse(
                        date + " " + time
                );

            } catch (ParseException ignored) {

                // Support older 12-hour times, e.g. 08:45 PM
                SimpleDateFormat dateTime12 =
                        new SimpleDateFormat(
                                "dd/MM/yyyy hh:mm a",
                                Locale.US
                        );

                dateTime12.setLenient(false);

                reminderDate = dateTime12.parse(
                        date + " " + time.toUpperCase(Locale.US)
                );
            }

            // Do not schedule reminders in the past
            if (reminderDate == null ||
                    reminderDate.getTime()
                            <= System.currentTimeMillis()) {

                return false;
            }

            // ==========================================
            // CREATE USER-OWNED REMINDER
            // ==========================================

            Intent intent = new Intent(
                    context,
                    ReminderReceiver.class
            );

            intent.putExtra("title", title);
            intent.putExtra("message", message);

            // The receiver will use this to check ownership
            intent.putExtra("user_id", userId);

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE
                    );

            // ==========================================
            // SCHEDULE ALARM
            // ==========================================

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(
                            Context.ALARM_SERVICE
                    );

            if (alarmManager == null) {
                return false;
            }

            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderDate.getTime(),
                    pendingIntent
            );

            return true;

        } catch (ParseException | SecurityException e) {

            return false;
        }
    }

    // ==========================================
    // CANCEL REMINDER
    // ==========================================

    public static void cancelReminder(
            Context context,
            int requestCode) {

        Intent intent = new Intent(
                context,
                ReminderReceiver.class
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_NO_CREATE
                                | PendingIntent.FLAG_IMMUTABLE
                );

        if (pendingIntent != null) {

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(
                            Context.ALARM_SERVICE
                    );

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }

            pendingIntent.cancel();
        }
    }
}

package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RemindScheduler {
    public static boolean scheduleReminder(
            Context context,
            String date,
            String time,
            String title,
            String message,
            int requestCode) {

        try {
            // Read the date and time entered in the form

            Date reminderDate = null;

// First try the new 24-hour format, such as 20:45.
            SimpleDateFormat dateTime24 =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            dateTime24.setLenient(false);

            try {
                reminderDate = dateTime24.parse(date + " " + time);
            } catch (ParseException ignored) {
                // Try the older 12-hour format for existing appointments.
                SimpleDateFormat dateTime12 =
                        new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
                dateTime12.setLenient(false);
                reminderDate = dateTime12.parse(
                        date + " " + time.toUpperCase(Locale.US));
            }


            if (reminderDate == null ||
                    reminderDate.getTime() <= System.currentTimeMillis()) {
                return false;
            }

            // Prepare the notification receiver
            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT |
                            PendingIntent.FLAG_IMMUTABLE
            );

            // Schedule the reminder
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(
                            Context.ALARM_SERVICE);

            if (alarmManager == null) {
                return false;
            }

            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderDate.getTime(),
                    pendingIntent
            );

            return true;

        } catch (ParseException e) {
            return false;
        }
    }
    public static void cancelReminder(Context context, int requestCode) {
        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }

            pendingIntent.cancel();
        }
    }
}


package com.example.medisync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private int userId = -1;

    private LinearLayout layoutUpcomingReminders;
    private TextView tvNoUpcomingReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // ==========================================
        // GET LOGGED-IN USER
        // ==========================================

        SharedPreferences accountPrefs =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPrefs.getString(
                "username", ""
        );

        userId = dbHelper.getUserId(username);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        // ==========================================
        // INITIALIZE DASHBOARD VIEWS
        // ==========================================

        layoutUpcomingReminders =
                findViewById(R.id.layoutUpcomingReminders);

        tvNoUpcomingReminders =
                findViewById(R.id.tvNoUpcomingReminders);

        View btnRecord = findViewById(R.id.btnRecord);
        View btnReminders = findViewById(R.id.btnReminders);
        View btnResolve = findViewById(R.id.btnResolve);
        View btnEmergency = findViewById(R.id.btnEmergency);

        View navHome = findViewById(R.id.navHome);
        View navRecords = findViewById(R.id.navRecords);
        View navReminders = findViewById(R.id.navReminders);
        View navAccount = findViewById(R.id.navAccount);

        View btnChatbot = findViewById(R.id.cardMediBot);

        // ==========================================
        // RECORD INFORMATION
        // ==========================================

        btnRecord.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecordActivity.class
            );

            startActivity(intent);
        });

        // ==========================================
        // REMINDERS
        // ==========================================

        btnReminders.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ReminderActivity.class
            );

            startActivity(intent);
        });

        // ==========================================
        // PROBLEM RESOLVER
        // ==========================================

        btnResolve.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ResolverActivity.class
            );

            startActivity(intent);
        });

        // ==========================================
        // MEDIBOT
        // ==========================================

        btnChatbot.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ChatbotActivity.class
            );

            startActivity(intent);
        });

        // ==========================================
        // EMERGENCY CONTACT
        // ==========================================

        btnEmergency.setOnClickListener(v ->
                openEmergencyContact()
        );

        // ==========================================
        // BOTTOM NAVIGATION
        // ==========================================

        navHome.setOnClickListener(v ->
                showMessage("You are already on Home")
        );

        navRecords.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecordActivity.class
            );

            startActivity(intent);
        });

        navReminders.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ReminderActivity.class
            );

            startActivity(intent);
        });

        navAccount.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AccountActivity.class
            );

            startActivity(intent);
        });

        loadUpcomingReminders();
    }

    // ==========================================
    // REFRESH DASHBOARD
    // ==========================================

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper == null ||
                layoutUpcomingReminders == null ||
                userId <= 0) {
            return;
        }

        // Verify the account is still the same
        String username = getSharedPreferences(
                "MediSyncAccount",
                MODE_PRIVATE
        ).getString("username", "");

        int currentUserId = dbHelper.getUserId(username);

        if (currentUserId != userId) {

            Intent intent = new Intent(
                    this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        loadUpcomingReminders();
    }

    private void showMessage(String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // ==========================================
    // OPEN USER'S EMERGENCY CONTACT
    // ==========================================

    private void openEmergencyContact() {

        String name = "";
        String phone = "";

        // Get contacts belonging only to this user
        Cursor cursor =
                dbHelper.getAllEmergencyContacts(userId);

        try {

            // Use the most recently saved valid contact
            while (cursor.moveToNext()) {

                String savedPhone = cursor.getString(
                        cursor.getColumnIndexOrThrow("phone")
                );

                if (savedPhone != null &&
                        !savedPhone.trim().isEmpty()) {

                    name = cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

                    phone = savedPhone.trim();
                }
            }

        } finally {
            cursor.close();
        }

        if (phone.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please save an emergency contact first",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(
                    MainActivity.this,
                    EmergencyContactActivity.class
            );

            startActivity(intent);
            return;
        }

        Intent dialIntent = new Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", phone, null)
        );

        // Open dialer without automatically placing a call
        if (dialIntent.resolveActivity(
                getPackageManager()) != null) {

            startActivity(dialIntent);

            Toast.makeText(
                    this,
                    "Emergency contact: " + name,
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "No phone dialer available",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ==========================================
    // LOAD UPCOMING REMINDERS
    // ==========================================

    private void loadUpcomingReminders() {

        if (userId <= 0) {
            return;
        }

        layoutUpcomingReminders.removeAllViews();

        int count = 0;

        // ==========================================
        // LOAD USER'S MEDICINES
        // ==========================================

        Cursor medicineCursor =
                dbHelper.getAllMedicines(userId);

        try {

            while (medicineCursor.moveToNext()) {

                String name = medicineCursor.getString(
                        medicineCursor.getColumnIndexOrThrow(
                                "name"
                        )
                );

                String startDate = medicineCursor.getString(
                        medicineCursor.getColumnIndexOrThrow(
                                "start_date"
                        )
                );

                String endDate = medicineCursor.getString(
                        medicineCursor.getColumnIndexOrThrow(
                                "end_date"
                        )
                );

                String time = medicineCursor.getString(
                        medicineCursor.getColumnIndexOrThrow(
                                "reminder_time"
                        )
                );

                if (isMedicineUpcoming(
                        startDate,
                        endDate,
                        time)) {

                    addReminderItem(
                            "Medicine: " + name,
                            "Time: " + time
                    );

                    count++;
                }
            }

        } finally {
            medicineCursor.close();
        }

        // ==========================================
        // LOAD USER'S APPOINTMENTS
        // ==========================================

        Cursor appointmentCursor =
                dbHelper.getAllAppointments(userId);

        try {

            while (appointmentCursor.moveToNext()) {

                String title = appointmentCursor.getString(
                        appointmentCursor.getColumnIndexOrThrow(
                                "title"
                        )
                );

                String date = appointmentCursor.getString(
                        appointmentCursor.getColumnIndexOrThrow(
                                "appointment_date"
                        )
                );

                String time = appointmentCursor.getString(
                        appointmentCursor.getColumnIndexOrThrow(
                                "appointment_time"
                        )
                );

                if (isAppointmentUpcoming(date, time)) {

                    addReminderItem(
                            title,
                            "Date: " + date
                                    + "   Time: " + time
                    );

                    count++;
                }
            }

        } finally {
            appointmentCursor.close();
        }

        // ==========================================
        // EMPTY REMINDER MESSAGE
        // ==========================================

        if (count == 0) {

            TextView emptyMessage = new TextView(this);

            emptyMessage.setText(
                    "No upcoming reminders found."
            );

            emptyMessage.setTextColor(
                    android.graphics.Color.parseColor(
                            "#426D68"
                    )
            );

            emptyMessage.setTextSize(13);

            layoutUpcomingReminders.addView(
                    emptyMessage
            );
        }
    }

    // ==========================================
    // DISPLAY REMINDER ITEM
    // ==========================================

    private void addReminderItem(
            String title,
            String details) {

        TextView item = new TextView(this);

        item.setText(
                title + "\n" + details
        );

        item.setTextColor(
                android.graphics.Color.parseColor(
                        "#205C56"
                )
        );

        item.setTextSize(14);

        item.setPadding(12, 12, 12, 12);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.bottomMargin = 8;

        item.setLayoutParams(params);

        item.setBackgroundColor(
                android.graphics.Color.parseColor(
                        "#E8F6F2"
                )
        );

        layoutUpcomingReminders.addView(item);
    }

    // ==========================================
    // CHECK UPCOMING MEDICINE
    // ==========================================

    private boolean isMedicineUpcoming(
            String startDate,
            String endDate,
            String time) {

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.US
                    );

            dateFormat.setLenient(false);

            Date today = dateFormat.parse(
                    dateFormat.format(new Date())
            );

            // Medicines without dates can still be active
            if (startDate == null ||
                    startDate.trim().isEmpty()) {

                startDate = dateFormat.format(new Date());
            }

            Date start = dateFormat.parse(startDate);

            if (today == null || start == null) {
                return false;
            }

            if (today.before(start)) {
                return false;
            }

            // If no end date exists, treat medicine as ongoing
            if (endDate == null ||
                    endDate.trim().isEmpty()) {

                return true;
            }

            Date end = dateFormat.parse(endDate);

            return end != null && !today.after(end);

        } catch (Exception e) {

            return false;
        }
    }

    // ==========================================
    // CHECK UPCOMING APPOINTMENT
    // ==========================================

    private boolean isAppointmentUpcoming(
            String date,
            String time) {

        if (date == null || time == null) {
            return false;
        }

        String dateTime = date + " " + time;

        // First try the new 24-hour format
        SimpleDateFormat format24 =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.US
                );

        format24.setLenient(false);

        try {

            Date appointmentDate =
                    format24.parse(dateTime);

            return appointmentDate != null
                    && appointmentDate.after(new Date());

        } catch (Exception ignored) {
            // Try the older format
        }

        // Support older 12-hour appointment times
        SimpleDateFormat format12 =
                new SimpleDateFormat(
                        "dd/MM/yyyy hh:mm a",
                        Locale.US
                );

        format12.setLenient(false);

        try {

            Date appointmentDate =
                    format12.parse(
                            dateTime.toUpperCase(Locale.US)
                    );

            return appointmentDate != null
                    && appointmentDate.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}

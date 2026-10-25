
package com.example.medisync;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    TextView btnAccountDetails, btnChangePassword,
            btnAbout, btnLogout, btnAppSettings;

    TextView tvAccountName, tvAccountUsername;

    DatabaseHelper dbHelper;

    int userId = -1;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        dbHelper = new DatabaseHelper(this);

        // ==========================================
        // GET LOGGED-IN USER
        // ==========================================

        SharedPreferences accountPrefs =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        username = accountPrefs.getString(
                "username", ""
        );

        userId = dbHelper.getUserId(username);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ==========================================
        // INITIALIZE VIEWS
        // ==========================================

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountUsername = findViewById(R.id.tvAccountUsername);

        btnAccountDetails = findViewById(R.id.btnAccountDetails);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        btnAppSettings = findViewById(R.id.btnAppSettings);

        loadAccountInfo();

        // ==========================================
        // ACCOUNT DETAILS
        // ==========================================

        btnAccountDetails.setOnClickListener(v -> {

            Cursor cursor = dbHelper.getUserDetails(username);

            try {

                if (cursor != null && cursor.moveToFirst()) {

                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

                    String savedUsername = cursor.getString(
                            cursor.getColumnIndexOrThrow("username")
                    );

                    new AlertDialog.Builder(this)
                            .setTitle("Account Details")
                            .setMessage(
                                    "Name: " + name
                                            + "\nUsername: "
                                            + savedUsername
                            )
                            .setPositiveButton("OK", null)
                            .show();

                } else {

                    Toast.makeText(
                            this,
                            "Account details not found",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        // ==========================================
        // CHANGE PASSWORD
        // ==========================================

        btnChangePassword.setOnClickListener(v -> {

            LinearLayout layout = new LinearLayout(this);

            layout.setOrientation(
                    LinearLayout.VERTICAL
            );

            int padding = (int) (
                    20 * getResources()
                            .getDisplayMetrics().density
            );

            layout.setPadding(
                    padding,
                    padding,
                    padding,
                    0
            );

            EditText etCurrent = new EditText(this);

            etCurrent.setHint("Current password");

            etCurrent.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            layout.addView(etCurrent);

            EditText etNew = new EditText(this);

            etNew.setHint(
                    "New password (minimum 6 characters)"
            );

            etNew.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            layout.addView(etNew);

            EditText etConfirm = new EditText(this);

            etConfirm.setHint("Confirm new password");

            etConfirm.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            layout.addView(etConfirm);

            new AlertDialog.Builder(this)
                    .setTitle("Change Password")
                    .setView(layout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(
                            "Update",
                            (dialog, which) -> {

                                String current =
                                        etCurrent.getText()
                                                .toString();

                                String newPass =
                                        etNew.getText()
                                                .toString();

                                String confirm =
                                        etConfirm.getText()
                                                .toString();

                                if (current.isEmpty()
                                        || newPass.isEmpty()
                                        || confirm.isEmpty()) {

                                    Toast.makeText(
                                            this,
                                            "Please fill in all fields",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                if (newPass.length() < 6) {

                                    Toast.makeText(
                                            this,
                                            "New password must contain at least 6 characters",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                if (!newPass.equals(confirm)) {

                                    Toast.makeText(
                                            this,
                                            "New passwords do not match",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                boolean updated =
                                        dbHelper.changePassword(
                                                username,
                                                current,
                                                newPass
                                        );

                                Toast.makeText(
                                        this,
                                        updated
                                                ? "Password changed successfully"
                                                : "Current password is incorrect",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    )
                    .show();
        });

        // ==========================================
        // ABOUT MEDISYNC
        // ==========================================

        btnAbout.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("About MediSync")
                    .setMessage(
                            "MediSync - Your Personal Health Companion\n\n"
                                    + "MediSync helps you manage your health "
                                    + "information in one place.\n\n"
                                    + "Features:\n"
                                    + "• Health records\n"
                                    + "• Medicine reminders\n"
                                    + "• Appointment reminders\n"
                                    + "• Emergency contacts\n"
                                    + "• Health Problem Resolver\n\n"
                                    + "Version 1.0"
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });

        // ==========================================
        // USER-SPECIFIC APP SETTINGS
        // ==========================================

        btnAppSettings.setOnClickListener(v -> {

            SharedPreferences settings =
                    getSharedPreferences(
                            "MediSyncSettings_" + userId,
                            MODE_PRIVATE
                    );

            LinearLayout layout = new LinearLayout(this);

            layout.setOrientation(
                    LinearLayout.VERTICAL
            );

            int padding = (int) (
                    20 * getResources()
                            .getDisplayMetrics().density
            );

            layout.setPadding(
                    padding,
                    padding,
                    padding,
                    0
            );

            Switch medicineSwitch = new Switch(this);

            medicineSwitch.setText("Medicine Reminders");

            medicineSwitch.setChecked(
                    settings.getBoolean(
                            "medicine_reminders",
                            true
                    )
            );

            layout.addView(medicineSwitch);

            Switch appointmentSwitch = new Switch(this);

            appointmentSwitch.setText(
                    "Appointment Reminders"
            );

            appointmentSwitch.setChecked(
                    settings.getBoolean(
                            "appointment_reminders",
                            true
                    )
            );

            layout.addView(appointmentSwitch);

            new AlertDialog.Builder(this)
                    .setTitle("App Settings")
                    .setMessage(
                            "Choose your reminder preferences."
                    )
                    .setView(layout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(
                            "Save",
                            (dialog, which) -> {

                                boolean medicineEnabled =
                                        medicineSwitch.isChecked();

                                boolean appointmentEnabled =
                                        appointmentSwitch.isChecked();

                                settings.edit()
                                        .putBoolean(
                                                "medicine_reminders",
                                                medicineEnabled
                                        )
                                        .putBoolean(
                                                "appointment_reminders",
                                                appointmentEnabled
                                        )
                                        .apply();

                                // Apply medicine reminder setting
                                if (medicineEnabled) {

                                    scheduleSavedMedicineReminder();

                                } else {

                                    RemindScheduler.cancelReminder(
                                            this,
                                            RemindScheduler
                                                    .getMedicineRequestCode(userId)
                                    );
                                }

                                // Apply appointment reminder setting
                                if (appointmentEnabled) {

                                    scheduleSavedAppointmentReminders();

                                } else {

                                    cancelSavedAppointmentReminders();
                                }

                                Toast.makeText(
                                        this,
                                        "Settings saved successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    )
                    .show();
        });

        // ==========================================
        // LOGOUT
        // ==========================================

        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage(
                            "Are you sure you want to log out?"
                    )
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(
                            "Log Out",
                            (dialog, which) -> {

                                // Remove displayed reminder notifications
                                // before switching accounts
                                NotificationManager manager =
                                        (NotificationManager)
                                                getSystemService(
                                                        Context.NOTIFICATION_SERVICE
                                                );

                                if (manager != null) {
                                    manager.cancelAll();
                                }

                                // Clear login session only.
                                // Keep this user's health data and settings.
                                getSharedPreferences(
                                        "MediSyncAccount",
                                        MODE_PRIVATE
                                )
                                        .edit()
                                        .clear()
                                        .apply();

                                Intent intent = new Intent(
                                        AccountActivity.this,
                                        LoginActivity.class
                                );

                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );

                                startActivity(intent);
                                finish();
                            }
                    )
                    .show();
        });
    }

    // ==========================================
    // DISPLAY LOGGED-IN USER DETAILS
    // ==========================================

    private void loadAccountInfo() {

        Cursor cursor = dbHelper.getUserDetails(username);

        try {

            if (cursor != null && cursor.moveToFirst()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String savedUsername = cursor.getString(
                        cursor.getColumnIndexOrThrow("username")
                );

                tvAccountName.setText(name);

                tvAccountUsername.setText(
                        "@" + savedUsername
                );

            } else {

                tvAccountName.setText("My Account");

                tvAccountUsername.setText(
                        "Account details not found"
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ==========================================
    // SCHEDULE USER'S SAVED MEDICINE REMINDER
    // ==========================================

    private void scheduleSavedMedicineReminder() {

        SharedPreferences medicineData =
                getSharedPreferences(
                        "MedicineData_" + userId,
                        MODE_PRIVATE
                );

        SharedPreferences reminderData =
                getSharedPreferences(
                        "MedicineReminderData_" + userId,
                        MODE_PRIVATE
                );

        String name = medicineData.getString(
                "medicineName", ""
        );

        String dosage = medicineData.getString(
                "dosage", ""
        );

        String date = reminderData.getString(
                "date", ""
        );

        String time = reminderData.getString(
                "time", ""
        );

        String description = reminderData.getString(
                "description", ""
        );

        if (!name.isEmpty()
                && !date.isEmpty()
                && !time.isEmpty()) {

            RemindScheduler.scheduleReminder(
                    this,
                    date,
                    time,
                    "Medicine Reminder",
                    "Time to take " + name
                            + " (" + dosage + "). "
                            + description,
                    RemindScheduler.getMedicineRequestCode(userId)
            );
        }
    }

    // ==========================================
    // CANCEL USER'S APPOINTMENT REMINDERS
    // ==========================================

    private void cancelSavedAppointmentReminders() {

        Cursor cursor = dbHelper.getAllAppointments(userId);

        try {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                RemindScheduler.cancelReminder(
                        this,
                        id
                );
            }

        } finally {
            cursor.close();
        }
    }

    // ==========================================
    // SCHEDULE USER'S APPOINTMENT REMINDERS
    // ==========================================

    private void scheduleSavedAppointmentReminders() {

        Cursor cursor = dbHelper.getAllAppointments(userId);

        try {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String doctor = cursor.getString(
                        cursor.getColumnIndexOrThrow("doctor_name")
                );

                String hospital = cursor.getString(
                        cursor.getColumnIndexOrThrow("hospital_name")
                );

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_date")
                );

                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_time")
                );

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")
                );

                RemindScheduler.scheduleReminder(
                        this,
                        date,
                        time,
                        "Appointment Reminder",
                        "Appointment with Dr. " + doctor
                                + " at " + hospital + ". "
                                + (description == null
                                ? ""
                                : description),
                        id
                );
            }

        } finally {
            cursor.close();
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

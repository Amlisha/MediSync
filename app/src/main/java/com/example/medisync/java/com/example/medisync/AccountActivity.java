package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    android.widget.TextView btnAccountDetails, btnChangePassword,
            btnAbout, btnLogout,btnAppSettings;
    TextView tvAccountName, tvAccountUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountUsername = findViewById(R.id.tvAccountUsername);

        loadAccountInfo();
        btnAccountDetails = findViewById(R.id.btnAccountDetails);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        btnAppSettings = findViewById(R.id.btnAppSettings);

        btnAccountDetails.setOnClickListener(v -> {
            String username = getSharedPreferences(
                    "MediSyncAccount", MODE_PRIVATE)
                    .getString("username", "");

            if (username.isEmpty()) {
                Toast.makeText(this,
                        "Please login again",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            android.database.Cursor cursor =
                    dbHelper.getUserDetails(username);

            if (cursor.moveToFirst()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));
                String savedUsername = cursor.getString(
                        cursor.getColumnIndexOrThrow("username"));

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Account Details")
                        .setMessage("Name: " + name
                                + "\nUsername: " + savedUsername)
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Toast.makeText(this,
                        "Account details not found",
                        Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            dbHelper.close();
        });

        btnChangePassword.setOnClickListener(v -> {
            android.widget.LinearLayout layout =
                    new android.widget.LinearLayout(this);
            layout.setOrientation(
                    android.widget.LinearLayout.VERTICAL);
            int padding = (int) (20 * getResources()
                    .getDisplayMetrics().density);
            layout.setPadding(padding, padding, padding, 0);

            android.widget.EditText etCurrent =
                    new android.widget.EditText(this);
            etCurrent.setHint("Current password");
            etCurrent.setInputType(
                    129);
            layout.addView(etCurrent);

            android.widget.EditText etNew =
                    new android.widget.EditText(this);
            etNew.setHint("New password (minimum 6 characters)");
            etNew.setInputType(129);
            layout.addView(etNew);

            android.widget.EditText etConfirm =
                    new android.widget.EditText(this);
            etConfirm.setHint("Confirm new password");
            etConfirm.setInputType(129);
            layout.addView(etConfirm);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Change Password")
                    .setView(layout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String current = etCurrent.getText()
                                .toString();
                        String newPass = etNew.getText()
                                .toString();
                        String confirm = etConfirm.getText()
                                .toString();

                        if (current.isEmpty() || newPass.isEmpty()
                                || confirm.isEmpty()) {
                            Toast.makeText(this,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (newPass.length() < 6) {
                            Toast.makeText(this,
                                    "New password must contain at least 6 characters",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newPass.equals(confirm)) {
                            Toast.makeText(this,
                                    "New passwords do not match",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String username = getSharedPreferences(
                                "MediSyncAccount", MODE_PRIVATE)
                                .getString("username", "");

                        if (username.isEmpty()) {
                            Toast.makeText(this,
                                    "Please login again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DatabaseHelper db = new DatabaseHelper(this);
                        boolean updated = db.changePassword(
                                username, current, newPass);
                        db.close();

                        Toast.makeText(this,
                                updated
                                        ? "Password changed successfully"
                                        : "Current password is incorrect",
                                Toast.LENGTH_LONG).show();
                    })
                    .show();
        });

        btnAbout.setOnClickListener(v ->
                Toast.makeText(this,
                        "MediSync - Your Personal Health Companion",
                        Toast.LENGTH_LONG).show());btnAbout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
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
                                    + "Version 1.0")
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnAppSettings.setOnClickListener(v -> {

            android.content.SharedPreferences settings =
                    getSharedPreferences("MediSyncSettings", MODE_PRIVATE);

            android.widget.LinearLayout layout =
                    new android.widget.LinearLayout(this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);

            int padding = (int) (20 * getResources()
                    .getDisplayMetrics().density);
            layout.setPadding(padding, padding, padding, 0);

            android.widget.Switch medicineSwitch =
                    new android.widget.Switch(this);
            medicineSwitch.setText("Medicine Reminders");
            medicineSwitch.setChecked(
                    settings.getBoolean("medicine_reminders", true));
            layout.addView(medicineSwitch);

            android.widget.Switch appointmentSwitch =
                    new android.widget.Switch(this);
            appointmentSwitch.setText("Appointment Reminders");
            appointmentSwitch.setChecked(
                    settings.getBoolean("appointment_reminders", true));
            layout.addView(appointmentSwitch);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("App Settings")
                    .setMessage("Choose your reminder preferences.")
                    .setView(layout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Save", (dialog, which) -> {

                        boolean medicineEnabled =
                                medicineSwitch.isChecked();
                        boolean appointmentEnabled =
                                appointmentSwitch.isChecked();

                        settings.edit()
                                .putBoolean("medicine_reminders",
                                        medicineEnabled)
                                .putBoolean("appointment_reminders",
                                        appointmentEnabled)
                                .apply();

                        // Apply medicine reminder preference.
                        if (medicineEnabled) {
                            scheduleSavedMedicineReminder();
                        } else {
                            RemindScheduler.cancelReminder(this, 1001);
                        }

                        // Apply appointment reminder preference.
                        if (appointmentEnabled) {
                            scheduleSavedAppointmentReminders();
                        } else {
                            cancelSavedAppointmentReminders();
                        }

                        Toast.makeText(this,
                                "Settings saved successfully",
                                Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Log Out", (dialog, which) -> {

                        // Clear the saved login session
                        getSharedPreferences(
                                "MediSyncAccount", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        // Return to Login screen
                        Intent intent = new Intent(
                                AccountActivity.this,
                                LoginActivity.class);

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    })
                    .show();
        });
    }
    private void loadAccountInfo() {

        String username = getSharedPreferences(
                "MediSyncAccount", MODE_PRIVATE)
                .getString("username", "");

        if (username.isEmpty()) {
            tvAccountName.setText("My Account");
            tvAccountUsername.setText("Please login again");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        android.database.Cursor cursor =
                dbHelper.getUserDetails(username);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                String savedUsername = cursor.getString(
                        cursor.getColumnIndexOrThrow("username"));

                tvAccountName.setText(name);
                tvAccountUsername.setText("@" + savedUsername);
            }

            cursor.close();
        }

        dbHelper.close();
    }

    private void scheduleSavedMedicineReminder() {

        android.content.SharedPreferences medicineData =
                getSharedPreferences("MedicineData", MODE_PRIVATE);

        android.content.SharedPreferences reminderData =
                getSharedPreferences("MedicineReminderData", MODE_PRIVATE);

        String name = medicineData.getString("medicineName", "");
        String dosage = medicineData.getString("dosage", "");
        String date = reminderData.getString("date", "");
        String time = reminderData.getString("time", "");
        String description =
                reminderData.getString("description", "");

        if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
            RemindScheduler.scheduleReminder(
                    this,
                    date,
                    time,
                    "Medicine Reminder",
                    "Time to take " + name + " (" + dosage + "). "
                            + description,
                    1001
            );
        }
    }

    private void cancelSavedAppointmentReminders() {

        DatabaseHelper db = new DatabaseHelper(this);
        android.database.Cursor cursor = db.getAllAppointments();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                RemindScheduler.cancelReminder(this, id);
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    private void scheduleSavedAppointmentReminders() {

        DatabaseHelper db = new DatabaseHelper(this);
        android.database.Cursor cursor = db.getAllAppointments();

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String doctor = cursor.getString(
                        cursor.getColumnIndexOrThrow("doctor_name"));

                String hospital = cursor.getString(
                        cursor.getColumnIndexOrThrow("hospital_name"));

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_date"));

                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_time"));

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("notes"));

                RemindScheduler.scheduleReminder(
                        this,
                        date,
                        time,
                        "Appointment Reminder",
                        "Appointment with Dr. " + doctor
                                + " at " + hospital + ". "
                                + (description == null ? "" : description),
                        id
                );
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

}


package com.example.medisync;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicineReminderActivity extends AppCompatActivity {

    TextView tvMedicineInfo;
    EditText etReminderDate, etReminderTime, etReminderDescription;
    Button btnSaveMedicineReminder;

    DatabaseHelper dbHelper;

    SharedPreferences medicinePreferences;
    SharedPreferences reminderPreferences;

    // Currently logged-in user
    int userId = -1;

    // Unique medicine reminder code for this user
    int medicineRequestCode;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medicine_reminder);

        tvMedicineInfo = findViewById(R.id.tvMedicineInfo);
        etReminderDate = findViewById(R.id.etReminderDate);
        etReminderTime = findViewById(R.id.etReminderTime);
        etReminderDescription =
                findViewById(R.id.etReminderDescription);
        btnSaveMedicineReminder =
                findViewById(R.id.btnSaveMedicineReminder);

        dbHelper = new DatabaseHelper(this);

        // ==========================================
        // GET LOGGED-IN USER
        // ==========================================

        SharedPreferences accountPreferences =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPreferences.getString(
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
        // USER-SPECIFIC PREFERENCES
        // ==========================================

        medicinePreferences =
                getSharedPreferences(
                        "MedicineData_" + userId,
                        MODE_PRIVATE
                );

        reminderPreferences =
                getSharedPreferences(
                        "MedicineReminderData_" + userId,
                        MODE_PRIVATE
                );

        // Each user has a different medicine alarm ID
        medicineRequestCode =
                RemindScheduler.getMedicineRequestCode(userId);

        dateFormat.setLenient(false);
        timeFormat.setLenient(false);

        // ==========================================
        // NOTIFICATION PERMISSION
        // ==========================================

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    101
            );
        }

        // ==========================================
        // LOAD SAVED INFORMATION
        // ==========================================

        loadMedicine();
        loadReminder();

        etReminderDate.setOnClickListener(
                v -> showDatePicker()
        );

        etReminderTime.setOnClickListener(
                v -> showTimePicker()
        );

        btnSaveMedicineReminder.setOnClickListener(
                v -> saveReminder()
        );
    }

    // ==========================================
    // LOAD USER'S MEDICINE
    // ==========================================

    private void loadMedicine() {

        String name = medicinePreferences.getString(
                "medicineName", ""
        );

        String dosage = medicinePreferences.getString(
                "dosage", ""
        );

        if (name.isEmpty()) {

            tvMedicineInfo.setText(
                    "No medicine details saved yet. " +
                            "Please save a medicine in Record Information first."
            );

        } else {

            tvMedicineInfo.setText(
                    "Medicine: " + name +
                            "\nDosage: " + dosage
            );
        }
    }

    // ==========================================
    // LOAD USER'S SAVED REMINDER
    // ==========================================

    private void loadReminder() {

        etReminderDate.setText(
                reminderPreferences.getString(
                        "date", ""
                )
        );

        String savedTime =
                reminderPreferences.getString(
                        "time", ""
                );

        etReminderTime.setText(
                convertTo24Hour(savedTime)
        );

        etReminderDescription.setText(
                reminderPreferences.getString(
                        "description", ""
                )
        );
    }

    // ==========================================
    // CONVERT TIME TO 24-HOUR FORMAT
    // ==========================================

    private String convertTo24Hour(String savedTime) {

        if (savedTime == null ||
                savedTime.trim().isEmpty()) {
            return "";
        }

        try {

            Date parsed = timeFormat.parse(savedTime);

            if (parsed != null) {
                return timeFormat.format(parsed);
            }

        } catch (ParseException ignored) {
            // Try the older 12-hour format
        }

        SimpleDateFormat oldFormat =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.US
                );

        oldFormat.setLenient(false);

        try {

            Date parsed = oldFormat.parse(
                    savedTime.toUpperCase(Locale.US)
            );

            if (parsed != null) {
                return timeFormat.format(parsed);
            }

        } catch (ParseException ignored) {
            // Invalid saved time
        }

        return "";
    }

    // ==========================================
    // DATE PICKER
    // ==========================================

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        String currentDate = etReminderDate.getText()
                .toString().trim();

        if (!currentDate.isEmpty()) {

            try {

                Date parsedDate =
                        dateFormat.parse(currentDate);

                if (parsedDate != null) {
                    calendar.setTime(parsedDate);
                }

            } catch (ParseException ignored) {
                // Use today's date
            }
        }

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            Calendar selected =
                                    Calendar.getInstance();

                            selected.set(
                                    year,
                                    month,
                                    dayOfMonth
                            );

                            etReminderDate.setText(
                                    dateFormat.format(
                                            selected.getTime()
                                    )
                            );
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        // Do not allow past dates
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        dialog.getDatePicker().setMinDate(
                today.getTimeInMillis()
        );

        dialog.show();
    }

    // ==========================================
    // TIME PICKER
    // ==========================================

    private void showTimePicker() {

        Calendar calendar = Calendar.getInstance();

        String currentTime = etReminderTime.getText()
                .toString().trim();

        if (!currentTime.isEmpty()) {

            try {

                Date parsedTime =
                        timeFormat.parse(currentTime);

                if (parsedTime != null) {

                    Calendar saved =
                            Calendar.getInstance();

                    saved.setTime(parsedTime);

                    calendar.set(
                            Calendar.HOUR_OF_DAY,
                            saved.get(Calendar.HOUR_OF_DAY)
                    );

                    calendar.set(
                            Calendar.MINUTE,
                            saved.get(Calendar.MINUTE)
                    );
                }

            } catch (ParseException ignored) {
                // Use current time
            }
        }

        TimePickerDialog dialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            Calendar selected =
                                    Calendar.getInstance();

                            selected.set(
                                    Calendar.HOUR_OF_DAY,
                                    hourOfDay
                            );

                            selected.set(
                                    Calendar.MINUTE,
                                    minute
                            );

                            etReminderTime.setText(
                                    timeFormat.format(
                                            selected.getTime()
                                    )
                            );
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );

        dialog.show();
    }

    // ==========================================
    // SAVE MEDICINE REMINDER
    // ==========================================

    private void saveReminder() {

        String name = medicinePreferences.getString(
                "medicineName", ""
        );

        String dosage = medicinePreferences.getString(
                "dosage", ""
        );

        String frequency = medicinePreferences.getString(
                "frequency", ""
        );

        String medicineTime = medicinePreferences.getString(
                "time", ""
        );

        String startDate = medicinePreferences.getString(
                "startDate", ""
        );

        String endDate = medicinePreferences.getString(
                "endDate", ""
        );

        String date = etReminderDate.getText()
                .toString().trim();

        String time = etReminderTime.getText()
                .toString().trim();

        String description =
                etReminderDescription.getText()
                        .toString().trim();

        // Check whether medicine details exist
        if (name.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please save medicine details first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Check reminder date and time
        if (date.isEmpty() || time.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select the reminder date and time",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ==========================================
        // VALIDATE DATE AND TIME
        // ==========================================

        try {

            Date selectedDate = dateFormat.parse(date);
            Date selectedTime = timeFormat.parse(time);

            if (selectedDate == null ||
                    selectedTime == null) {

                throw new ParseException(
                        "Invalid date or time",
                        0
                );
            }

            Calendar dateCalendar =
                    Calendar.getInstance();

            dateCalendar.setTime(selectedDate);

            Calendar timeCalendar =
                    Calendar.getInstance();

            timeCalendar.setTime(selectedTime);

            Calendar reminderCalendar =
                    Calendar.getInstance();

            reminderCalendar.set(
                    dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH),
                    dateCalendar.get(Calendar.DAY_OF_MONTH),
                    timeCalendar.get(Calendar.HOUR_OF_DAY),
                    timeCalendar.get(Calendar.MINUTE),
                    0
            );

            reminderCalendar.set(
                    Calendar.MILLISECOND,
                    0
            );

            // Reminder must be in the future
            if (reminderCalendar.getTimeInMillis()
                    <= System.currentTimeMillis()) {

                Toast.makeText(
                        this,
                        "Please select a future date and time",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

        } catch (ParseException e) {

            Toast.makeText(
                    this,
                    "Please select a valid date and time",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ==========================================
        // CREATE MEDICINE REMINDER OBJECT
        // ==========================================

        Medicine medicine = new Medicine(
                name,
                dosage,
                frequency,
                medicineTime,
                startDate,
                endDate
        );

        MedicineReminder reminder =
                new MedicineReminder(
                        medicine,
                        date,
                        time,
                        description
                );

        // ==========================================
        // SAVE USER-SPECIFIC REMINDER DETAILS
        // ==========================================

        reminderPreferences.edit()
                .putString("date", date)
                .putString("time", time)
                .putString("description", description)
                .apply();

        reminder.displayReminder();

        // ==========================================
        // CHECK USER'S REMINDER SETTINGS
        // ==========================================

        SharedPreferences settings =
                getSharedPreferences(
                        "MediSyncSettings_" + userId,
                        MODE_PRIVATE
                );

        boolean remindersEnabled =
                settings.getBoolean(
                        "medicine_reminders",
                        true
                );

        if (!remindersEnabled) {

            RemindScheduler.cancelReminder(
                    this,
                    medicineRequestCode
            );

            Toast.makeText(
                    this,
                    "Details saved. Medicine reminders are turned off in App Settings.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ==========================================
        // SCHEDULE MEDICINE NOTIFICATION
        // ==========================================

        boolean scheduled =
                RemindScheduler.scheduleReminder(
                        this,
                        date,
                        time,
                        "Medicine Reminder",
                        "Time to take " + name
                                + " (" + dosage + "). "
                                + description,
                        medicineRequestCode
                );

        if (scheduled) {

            Toast.makeText(
                    this,
                    "Medicine reminder scheduled successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Details saved, but reminder was not scheduled. Check the date and time.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}


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

    SharedPreferences medicinePreferences;
    SharedPreferences reminderPreferences;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
        }

        tvMedicineInfo = findViewById(R.id.tvMedicineInfo);
        etReminderDate = findViewById(R.id.etReminderDate);
        etReminderTime = findViewById(R.id.etReminderTime);
        etReminderDescription =
                findViewById(R.id.etReminderDescription);
        btnSaveMedicineReminder =
                findViewById(R.id.btnSaveMedicineReminder);

        medicinePreferences =
                getSharedPreferences("MedicineData", MODE_PRIVATE);

        reminderPreferences =
                getSharedPreferences("MedicineReminderData", MODE_PRIVATE);

        dateFormat.setLenient(false);
        timeFormat.setLenient(false);

        loadMedicine();
        loadReminder();

        etReminderDate.setOnClickListener(v -> showDatePicker());
        etReminderTime.setOnClickListener(v -> showTimePicker());

        btnSaveMedicineReminder.setOnClickListener(v -> saveReminder());
    }

    private void loadMedicine() {
        String name = medicinePreferences.getString("medicineName", "");
        String dosage = medicinePreferences.getString("dosage", "");

        if (name.isEmpty()) {
            tvMedicineInfo.setText(
                    "No medicine details saved yet. Please save a medicine in Record Information first.");
        } else {
            tvMedicineInfo.setText(
                    "Medicine: " + name + "\nDosage: " + dosage);
        }
    }

    private void loadReminder() {
        etReminderDate.setText(
                reminderPreferences.getString("date", ""));

        String savedTime = reminderPreferences.getString("time", "");
        etReminderTime.setText(convertTo24Hour(savedTime));

        etReminderDescription.setText(
                reminderPreferences.getString("description", ""));
    }

    private String convertTo24Hour(String savedTime) {
        if (savedTime == null || savedTime.trim().isEmpty()) {
            return "";
        }

        try {
            Date parsed = timeFormat.parse(savedTime);
            if (parsed != null) {
                return timeFormat.format(parsed);
            }
        } catch (ParseException ignored) {
            // Try the older 12-hour format below.
        }

        SimpleDateFormat oldFormat =
                new SimpleDateFormat("hh:mm a", Locale.US);
        oldFormat.setLenient(false);

        try {
            Date parsed = oldFormat.parse(savedTime.toUpperCase(Locale.US));
            if (parsed != null) {
                return timeFormat.format(parsed);
            }
        } catch (ParseException ignored) {
            // An unrecognised saved time will be left blank.
        }

        return "";
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        String currentDate = etReminderDate.getText().toString().trim();

        if (!currentDate.isEmpty()) {
            try {
                Date parsedDate = dateFormat.parse(currentDate);
                if (parsedDate != null) {
                    calendar.setTime(parsedDate);
                }
            } catch (ParseException ignored) {
                // Use today's date if the saved date is invalid.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    etReminderDate.setText(dateFormat.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() -
                (System.currentTimeMillis() % 86400000L));

        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        String currentTime = etReminderTime.getText().toString().trim();

        if (!currentTime.isEmpty()) {
            try {
                Date parsedTime = timeFormat.parse(currentTime);
                if (parsedTime != null) {
                    Calendar saved = Calendar.getInstance();
                    saved.setTime(parsedTime);
                    calendar.set(Calendar.HOUR_OF_DAY,
                            saved.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE,
                            saved.get(Calendar.MINUTE));
                }
            } catch (ParseException ignored) {
                // Use the current time if the saved time is invalid.
            }
        }

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selected.set(Calendar.MINUTE, minute);

                    etReminderTime.setText(timeFormat.format(selected.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void saveReminder() {
        String name = medicinePreferences.getString("medicineName", "");
        String dosage = medicinePreferences.getString("dosage", "");
        String frequency = medicinePreferences.getString("frequency", "");
        String medicineTime = medicinePreferences.getString("time", "");
        String startDate = medicinePreferences.getString("startDate", "");
        String endDate = medicinePreferences.getString("endDate", "");

        String date = etReminderDate.getText().toString().trim();
        String time = etReminderTime.getText().toString().trim();
        String description =
                etReminderDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this,
                    "Please save medicine details first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this,
                    "Please select the reminder date and time",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate the selected date and 24-hour time.
        try {
            Date selectedDate = dateFormat.parse(date);
            Date selectedTime = timeFormat.parse(time);

            if (selectedDate == null || selectedTime == null) {
                throw new ParseException("Invalid date or time", 0);
            }

            Calendar reminderCalendar = Calendar.getInstance();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(selectedDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(selectedTime);

            reminderCalendar.set(
                    dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH),
                    dateCalendar.get(Calendar.DAY_OF_MONTH),
                    timeCalendar.get(Calendar.HOUR_OF_DAY),
                    timeCalendar.get(Calendar.MINUTE),
                    0
            );
            reminderCalendar.set(Calendar.MILLISECOND, 0);

            if (reminderCalendar.getTimeInMillis()
                    <= System.currentTimeMillis()) {
                Toast.makeText(this,
                        "Please select a future date and time",
                        Toast.LENGTH_LONG).show();
                return;
            }

        } catch (ParseException e) {
            Toast.makeText(this,
                    "Please select a valid date and time",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Medicine medicine = new Medicine(
                name, dosage, frequency, medicineTime, startDate, endDate);

        MedicineReminder reminder =
                new MedicineReminder(medicine, date, time, description);

        // Save the selected reminder details.
        reminderPreferences.edit()
                .putString("date", date)
                .putString("time", time)
                .putString("description", description)
                .apply();

        reminder.displayReminder();

        // Respect the Medicine Reminders setting.
        SharedPreferences settings =
                getSharedPreferences("MediSyncSettings", MODE_PRIVATE);

        boolean remindersEnabled =
                settings.getBoolean("medicine_reminders", true);

        if (!remindersEnabled) {
            RemindScheduler.cancelReminder(this, 1001);

            Toast.makeText(this,
                    "Details saved. Medicine reminders are turned off in App Settings.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        boolean scheduled = RemindScheduler.scheduleReminder(
                this,
                date,
                time,
                "Medicine Reminder",
                "Time to take " + name + " (" + dosage + "). " + description,
                1001
        );

        if (scheduled) {
            Toast.makeText(this,
                    "Medicine reminder scheduled successfully",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Details saved, but reminder was not scheduled. Check the date and time.",
                    Toast.LENGTH_LONG).show();
        }
    }
}

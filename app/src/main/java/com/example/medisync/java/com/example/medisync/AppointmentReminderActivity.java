
package com.example.medisync;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentReminderActivity extends AppCompatActivity {

    EditText etDoctorName, etHospitalName;
    EditText etAppointmentDate, etAppointmentTime;
    EditText etAppointmentDescription;
    Button btnSaveAppointment;

    DatabaseHelper dbHelper;
    SharedPreferences preferences;
    int appointmentId = -1;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reminder);

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

        dateFormat.setLenient(false);
        timeFormat.setLenient(false);

        etDoctorName = findViewById(R.id.etDoctorName);
        etHospitalName = findViewById(R.id.etHospitalName);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        etAppointmentTime = findViewById(R.id.etAppointmentTime);
        etAppointmentDescription =
                findViewById(R.id.etAppointmentDescription);
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment);

        dbHelper = new DatabaseHelper(this);

        preferences = getSharedPreferences(
                "AppointmentReminderData", MODE_PRIVATE);

        appointmentId = getIntent().getIntExtra("appointment_id", -1);

        if (appointmentId != -1) {
            loadSelectedAppointment();
            btnSaveAppointment.setText("Update Appointment");
        } else {
            clearForm();
            btnSaveAppointment.setText("Save Appointment");
        }

        etAppointmentDate.setOnClickListener(v -> showDatePicker());
        etAppointmentTime.setOnClickListener(v -> showTimePicker());

        btnSaveAppointment.setOnClickListener(v -> saveAppointment());
    }

    private void clearForm() {
        etDoctorName.setText("");
        etHospitalName.setText("");
        etAppointmentDate.setText("");
        etAppointmentTime.setText("");
        etAppointmentDescription.setText("");
    }

    private void loadSelectedAppointment() {
        Cursor cursor = dbHelper.getAllAppointments();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                if (id == appointmentId) {
                    etDoctorName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("doctor_name")));

                    etHospitalName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("hospital_name")));

                    etAppointmentDate.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("appointment_date")));

                    String savedTime = cursor.getString(
                            cursor.getColumnIndexOrThrow("appointment_time"));

                    etAppointmentTime.setText(convertTo24Hour(savedTime));

                    etAppointmentDescription.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("notes")));

                    return;
                }
            }
        } finally {
            cursor.close();
        }

        Toast.makeText(this, "Appointment not found",
                Toast.LENGTH_SHORT).show();
        finish();
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
            // Try the old format for existing appointments.
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
            // An invalid saved time will be left blank.
        }

        return "";
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        String currentDate = etAppointmentDate.getText().toString().trim();

        if (!currentDate.isEmpty()) {
            try {
                Date parsed = dateFormat.parse(currentDate);
                if (parsed != null) {
                    calendar.setTime(parsed);
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

                    etAppointmentDate.setText(
                            dateFormat.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(
                System.currentTimeMillis()
                        - (System.currentTimeMillis() % 86400000L));

        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        String currentTime = etAppointmentTime.getText().toString().trim();

        if (!currentTime.isEmpty()) {
            try {
                Date parsed = timeFormat.parse(currentTime);
                if (parsed != null) {
                    Calendar saved = Calendar.getInstance();
                    saved.setTime(parsed);

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

                    etAppointmentTime.setText(
                            timeFormat.format(selected.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void saveAppointment() {
        String doctor = etDoctorName.getText().toString().trim();
        String hospital = etHospitalName.getText().toString().trim();
        String date = etAppointmentDate.getText().toString().trim();
        String time = etAppointmentTime.getText().toString().trim();
        String description =
                etAppointmentDescription.getText().toString().trim();

        if (doctor.isEmpty() || hospital.isEmpty()
                || date.isEmpty() || time.isEmpty()) {

            Toast.makeText(this,
                    "Please fill in the doctor, hospital, date and time",
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

            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(selectedDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(selectedTime);

            Calendar reminderCalendar = Calendar.getInstance();
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

        boolean isEditing = appointmentId != -1;

        AppointmentReminders reminder = new AppointmentReminders(
                doctor, hospital, date, time, description);

        if (isEditing) {
            RemindScheduler.cancelReminder(this, appointmentId);
        }

        boolean success;

        if (isEditing) {
            success = dbHelper.updateAppointment(
                    appointmentId,
                    "Appointment with Dr. " + doctor,
                    doctor, hospital, date, time, description);
        } else {
            int newId = dbHelper.addAppointment(
                    "Appointment with Dr. " + doctor,
                    doctor, hospital, date, time, description);

            success = newId != -1;

            if (success) {
                appointmentId = newId;
            }
        }

        if (!success) {
            Toast.makeText(this, "Could not save appointment",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        preferences.edit()
                .putString("doctorName", reminder.getDoctorName())
                .putString("hospitalName", reminder.getHospitalName())
                .putString("date", reminder.getDate())
                .putString("time", reminder.getTime())
                .putString("description", reminder.getDescription())
                .apply();

        reminder.displayReminder();

        SharedPreferences settings = getSharedPreferences(
                "MediSyncSettings", MODE_PRIVATE);

        boolean remindersEnabled =
                settings.getBoolean("appointment_reminders", true);

        if (!remindersEnabled) {
            RemindScheduler.cancelReminder(this, appointmentId);

            Toast.makeText(this,
                    "Appointment saved. Appointment reminders are turned off in App Settings.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        boolean scheduled = RemindScheduler.scheduleReminder(
                this,
                date,
                time,
                "Appointment Reminder",
                "Appointment with Dr. " + doctor
                        + " at " + hospital + ". " + description,
                appointmentId
        );

        if (scheduled) {
            Toast.makeText(this,
                    isEditing
                            ? "Appointment updated and reminder scheduled"
                            : "Appointment saved and reminder scheduled",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Appointment saved, but reminder was not scheduled. Check the future date and time.",
                    Toast.LENGTH_LONG).show();
        }

        finish();
    }
}

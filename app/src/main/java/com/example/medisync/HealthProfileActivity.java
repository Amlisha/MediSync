
package com.example.medisync;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class HealthProfileActivity extends AppCompatActivity {

    EditText etAge, etAllergies, etMedicalNotes;
    Spinner etBloodGroup;
    Button btnSaveProfile;

    DatabaseHelper dbHelper;

    // ID of the currently logged-in user
    int userId = -1;

    // Store selected date of birth
    private int birthYear = -1;
    private int birthMonth = -1;
    private int birthDay = -1;

    // User-specific preferences for date of birth
    SharedPreferences profilePreferences;

    private final String[] bloodGroups = {
            "Select blood group",
            "A+", "A-", "B+", "B-",
            "AB+", "AB-", "O+", "O-", "Unknown"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep your existing layout name
        setContentView(R.layout.activity_health_prfile);

        etAge = findViewById(R.id.etAge);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etAllergies = findViewById(R.id.etAllergies);
        etMedicalNotes = findViewById(R.id.etMedicalNotes);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        dbHelper = new DatabaseHelper(this);

        // Get username of logged-in user
        SharedPreferences accountPreferences =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPreferences.getString(
                "username", ""
        );

        // Find user's database ID
        userId = dbHelper.getUserId(username);

        // Do not allow profile access without login
        if (userId <= 0) {
            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Each user gets separate DOB preferences
        profilePreferences = getSharedPreferences(
                "HealthProfileData_" + userId,
                MODE_PRIVATE
        );

        // Set up blood group dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                bloodGroups
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        etBloodGroup.setAdapter(adapter);

        // Open date picker when DOB field is clicked
        etAge.setOnClickListener(v -> openDatePicker());

        // Load current user's saved profile
        loadProfile();

        // Save current user's profile
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    // ==========================================
    // DATE OF BIRTH PICKER
    // ==========================================

    private void openDatePicker() {

        Calendar today = Calendar.getInstance();

        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        // If DOB already exists, show that date
        if (birthYear != -1) {
            year = birthYear;
            month = birthMonth;
            day = birthDay;
        }

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    birthYear = selectedYear;
                    birthMonth = selectedMonth;
                    birthDay = selectedDay;

                    // Calculate and display age
                    displayDateOfBirth();
                },
                year,
                month,
                day
        );

        // Future dates are not allowed
        picker.getDatePicker().setMaxDate(
                System.currentTimeMillis()
        );

        picker.show();
    }

    // ==========================================
    // CALCULATE AGE
    // ==========================================

    private int calculateAge() {

        if (birthYear == -1) {
            return -1;
        }

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthYear;

        // Subtract one if birthday has not occurred yet
        if (today.get(Calendar.MONTH) < birthMonth
                || (today.get(Calendar.MONTH) == birthMonth
                && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {

            age--;
        }

        return age;
    }

    // ==========================================
    // DISPLAY DATE OF BIRTH AND AGE
    // ==========================================

    private void displayDateOfBirth() {

        if (birthYear == -1) {
            return;
        }

        int age = calculateAge();

        String dateText = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d  (Age: %d)",
                birthDay,
                birthMonth + 1,
                birthYear,
                age
        );

        etAge.setText(dateText);
    }

    // ==========================================
    // SAVE HEALTH PROFILE
    // ==========================================

    private void saveProfile() {

        // Validate date of birth
        if (birthYear == -1) {
            Toast.makeText(
                    this,
                    "Please select your date of birth",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String bloodGroup =
                etBloodGroup.getSelectedItem().toString();

        // Validate blood group
        if (bloodGroup.equals("Select blood group")) {
            Toast.makeText(
                    this,
                    "Please select your blood group",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String allergies = etAllergies.getText()
                .toString().trim();

        String medicalNotes = etMedicalNotes.getText()
                .toString().trim();

        int age = calculateAge();

        // Validate age
        if (age < 0 || age > 120) {
            Toast.makeText(
                    this,
                    "Please select a valid date of birth",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Save profile for logged-in user only
        boolean success = dbHelper.saveHealthProfile(
                userId,
                age,
                bloodGroup,
                allergies,
                medicalNotes
        );

        if (success) {

            // Save DOB separately for this user
            profilePreferences.edit()
                    .putInt("birthYear", birthYear)
                    .putInt("birthMonth", birthMonth)
                    .putInt("birthDay", birthDay)
                    .apply();

            Toast.makeText(
                    this,
                    "Health profile saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Could not save health profile",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ==========================================
    // LOAD SAVED HEALTH PROFILE
    // ==========================================

    private void loadProfile() {

        // Load only the logged-in user's profile
        Cursor cursor = dbHelper.getHealthProfile(userId);

        if (cursor == null) {
            return;
        }

        try {

            if (cursor.moveToFirst()) {

                String bloodGroup = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "blood_group"
                        )
                );

                String allergies = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "allergies"
                        )
                );

                String medicalNotes = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "notes"
                        )
                );

                // Restore blood group dropdown selection
                int position = 0;

                for (int i = 0; i < bloodGroups.length; i++) {

                    if (bloodGroups[i].equals(bloodGroup)) {
                        position = i;
                        break;
                    }
                }

                etBloodGroup.setSelection(position);

                // Restore allergies and medical notes
                etAllergies.setText(
                        allergies == null ? "" : allergies
                );

                etMedicalNotes.setText(
                        medicalNotes == null ? "" : medicalNotes
                );

                // Restore this user's saved DOB
                birthYear = profilePreferences.getInt(
                        "birthYear", -1
                );

                birthMonth = profilePreferences.getInt(
                        "birthMonth", -1
                );

                birthDay = profilePreferences.getInt(
                        "birthDay", -1
                );

                if (birthYear != -1
                        && birthMonth >= 0
                        && birthMonth <= 11
                        && birthDay >= 1
                        && birthDay <= 31) {

                    displayDateOfBirth();
                }
            }

        } finally {
            cursor.close();
        }
    }
}

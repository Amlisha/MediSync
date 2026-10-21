
package com.example.medisync;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class HealthProfileActivity extends AppCompatActivity {

    EditText etAge, etAllergies, etMedicalNotes;
    Spinner etBloodGroup;
    Button btnSaveProfile;
    DatabaseHelper dbHelper;

    private int birthYear = -1;
    private int birthMonth = -1;
    private int birthDay = -1;

    private final String[] bloodGroups = {
            "Select blood group",
            "A+", "A-", "B+", "B-",
            "AB+", "AB-", "O+", "O-", "Unknown"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_prfile);

        etAge = findViewById(R.id.etAge);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etAllergies = findViewById(R.id.etAllergies);
        etMedicalNotes = findViewById(R.id.etMedicalNotes);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        dbHelper = new DatabaseHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                bloodGroups
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        etBloodGroup.setAdapter(adapter);

        etAge.setOnClickListener(v -> openDatePicker());

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void openDatePicker() {
        Calendar today = Calendar.getInstance();

        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    birthYear = selectedYear;
                    birthMonth = selectedMonth;
                    birthDay = selectedDay;

                    Calendar dob = Calendar.getInstance();
                    dob.set(selectedYear, selectedMonth, selectedDay);

                    int age = today.get(Calendar.YEAR) - selectedYear;

                    if (today.get(Calendar.DAY_OF_YEAR)
                            < dob.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    etAge.setText(
                            String.format(
                                    java.util.Locale.getDefault(),
                                    "%02d/%02d/%04d  (Age: %d)",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear,
                                    age
                            )
                    );
                },
                year, month, day
        );

        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.show();
    }

    private void saveProfile() {
        if (birthYear == -1) {
            Toast.makeText(this,
                    "Please select your date of birth",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String bloodGroup = etBloodGroup.getSelectedItem().toString();

        if (bloodGroup.equals("Select blood group")) {
            Toast.makeText(this,
                    "Please select your blood group",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String allergies = etAllergies.getText().toString().trim();
        String medicalNotes = etMedicalNotes.getText().toString().trim();

        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(birthYear, birthMonth, birthDay);

        int age = today.get(Calendar.YEAR) - birthYear;

        if (today.get(Calendar.MONTH) < birthMonth
                || (today.get(Calendar.MONTH) == birthMonth
                && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {
            age--;
        }

        if (age < 0 || age > 120) {
            Toast.makeText(this,
                    "Please select a valid date of birth",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.saveHealthProfile(
                age, bloodGroup, allergies, medicalNotes
        );

        if (success) {
            Toast.makeText(this,
                    "Health profile saved successfully",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Could not save health profile",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfile() {
        Cursor cursor = dbHelper.getHealthProfile();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String bloodGroup = cursor.getString(
                        cursor.getColumnIndexOrThrow("blood_group"));

                String allergies = cursor.getString(
                        cursor.getColumnIndexOrThrow("allergies"));

                String medicalNotes = cursor.getString(
                        cursor.getColumnIndexOrThrow("notes"));

                int position = 0;
                for (int i = 0; i < bloodGroups.length; i++) {
                    if (bloodGroups[i].equals(bloodGroup)) {
                        position = i;
                        break;
                    }
                }
                etBloodGroup.setSelection(position);

                etAllergies.setText(allergies);
                etMedicalNotes.setText(medicalNotes);
            }

            cursor.close();
        }
    }
}

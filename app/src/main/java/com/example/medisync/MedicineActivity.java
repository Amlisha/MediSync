
package com.example.medisync;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicineActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    EditText etMedicineName, etTime, etStartDate, etEndDate;
    EditText etCustomDosage, etCustomFrequency;
    Spinner etDosage, etFrequency;
    Button btnSaveMedicine;

    SharedPreferences preferences;

    int medicineId = -1;
    int userId = -1;

    String[] dosageOptions = {
            "Select dosage",
            "250 mg",
            "500 mg",
            "1 tablet",
            "2 tablets",
            "1 capsule",
            "5 ml",
            "10 ml",
            "Other (enter manually)"
    };

    String[] frequencyOptions = {
            "Select frequency",
            "Once daily",
            "Twice daily",
            "Three times daily",
            "Every 6 hours",
            "Every 8 hours",
            "As prescribed",
            "Other (enter manually)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

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

            finish();
            return;
        }

        // ==========================================
        // INITIALIZE VIEWS
        // ==========================================

        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        etFrequency = findViewById(R.id.etFrequency);
        etTime = findViewById(R.id.etTime);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);

        etCustomDosage = findViewById(R.id.etCustomDosage);
        etCustomFrequency = findViewById(R.id.etCustomFrequency);

        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        // Save medicine details separately for each user
        preferences = getSharedPreferences(
                "MedicineData_" + userId,
                MODE_PRIVATE
        );

        // ==========================================
        // DOSAGE DROPDOWN
        // ==========================================

        ArrayAdapter<String> dosageAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        dosageOptions
                );

        dosageAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        etDosage.setAdapter(dosageAdapter);

        // ==========================================
        // FREQUENCY DROPDOWN
        // ==========================================

        ArrayAdapter<String> frequencyAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        frequencyOptions
                );

        frequencyAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        etFrequency.setAdapter(frequencyAdapter);

        // ==========================================
        // CUSTOM DOSAGE FIELD
        // ==========================================

        etDosage.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        boolean isCustom =
                                position == dosageOptions.length - 1;

                        etCustomDosage.setVisibility(
                                isCustom ? View.VISIBLE : View.GONE
                        );

                        if (!isCustom) {
                            etCustomDosage.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        // ==========================================
        // CUSTOM FREQUENCY FIELD
        // ==========================================

        etFrequency.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        boolean isCustom =
                                position == frequencyOptions.length - 1;

                        etCustomFrequency.setVisibility(
                                isCustom ? View.VISIBLE : View.GONE
                        );

                        if (!isCustom) {
                            etCustomFrequency.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        // ==========================================
        // DATE AND TIME PICKERS
        // ==========================================

        etTime.setOnClickListener(
                v -> showTimePicker()
        );

        etStartDate.setOnClickListener(
                v -> showDatePicker(etStartDate)
        );

        etEndDate.setOnClickListener(
                v -> showDatePicker(etEndDate)
        );

        // ==========================================
        // ADD OR EDIT MEDICINE
        // ==========================================

        medicineId = getIntent().getIntExtra(
                "medicine_id", -1
        );

        if (medicineId != -1) {

            loadSelectedMedicine();

            btnSaveMedicine.setText("Update Medicine");

        } else {

            clearFields();

            btnSaveMedicine.setText("Save Medicine");
        }

        btnSaveMedicine.setOnClickListener(
                v -> saveMedicine()
        );
    }

    // ==========================================
    // TIME PICKER
    // ==========================================

    private void showTimePicker() {

        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {

                    Calendar selectedTime =
                            Calendar.getInstance();

                    selectedTime.set(
                            Calendar.HOUR_OF_DAY,
                            hourOfDay
                    );

                    selectedTime.set(
                            Calendar.MINUTE,
                            minute
                    );

                    SimpleDateFormat format =
                            new SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.getDefault()
                            );

                    etTime.setText(
                            format.format(selectedTime.getTime())
                    );
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        dialog.show();
    }

    // ==========================================
    // DATE PICKER
    // ==========================================

    private void showDatePicker(EditText target) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    String selectedDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                    );

                    target.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // ==========================================
    // GET SELECTED DOSAGE
    // ==========================================

    private String getDosageValue() {

        if (etDosage.getSelectedItemPosition()
                == dosageOptions.length - 1) {

            return etCustomDosage.getText()
                    .toString().trim();
        }

        return etDosage.getSelectedItemPosition() == 0
                ? ""
                : etDosage.getSelectedItem().toString();
    }

    // ==========================================
    // GET SELECTED FREQUENCY
    // ==========================================

    private String getFrequencyValue() {

        if (etFrequency.getSelectedItemPosition()
                == frequencyOptions.length - 1) {

            return etCustomFrequency.getText()
                    .toString().trim();
        }

        return etFrequency.getSelectedItemPosition() == 0
                ? ""
                : etFrequency.getSelectedItem().toString();
    }

    // ==========================================
    // VALIDATE MEDICINE DATES
    // ==========================================

    private boolean isEndDateValid(
            String startDate,
            String endDate) {

        if (startDate.isEmpty() || endDate.isEmpty()) {
            return true;
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        format.setLenient(false);

        try {

            Date start = format.parse(startDate);
            Date end = format.parse(endDate);

            return start != null
                    && end != null
                    && !end.before(start);

        } catch (ParseException e) {
            return false;
        }
    }

    // ==========================================
    // SAVE OR UPDATE MEDICINE
    // ==========================================

    private void saveMedicine() {

        String name = etMedicineName.getText()
                .toString().trim();

        String dosage = getDosageValue();
        String frequency = getFrequencyValue();

        String time = etTime.getText()
                .toString().trim();

        String startDate = etStartDate.getText()
                .toString().trim();

        String endDate = etEndDate.getText()
                .toString().trim();

        // Check required fields
        if (name.isEmpty()
                || dosage.isEmpty()
                || frequency.isEmpty()
                || time.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter medicine name, dosage, frequency and time",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Validate start and end dates
        if (!isEndDateValid(startDate, endDate)) {

            Toast.makeText(
                    this,
                    "End date must be on or after the start date",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ==========================================
        // SAVE TO USER'S DATABASE RECORDS
        // ==========================================

        boolean saved;

        if (medicineId != -1) {

            saved = dbHelper.updateMedicine(
                    userId,
                    medicineId,
                    name,
                    dosage,
                    frequency,
                    startDate,
                    endDate,
                    time
            );

        } else {

            saved = dbHelper.addMedicine(
                    userId,
                    name,
                    dosage,
                    frequency,
                    startDate,
                    endDate,
                    time
            );
        }

        if (!saved) {

            Toast.makeText(
                    this,
                    "Unable to save medicine",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ==========================================
        // SAVE USER-SPECIFIC MEDICINE PREFERENCES
        // ==========================================

        SharedPreferences.Editor editor =
                preferences.edit();

        editor.putString("medicineName", name);
        editor.putString("dosage", dosage);
        editor.putString("frequency", frequency);
        editor.putString("time", time);
        editor.putString("startDate", startDate);
        editor.putString("endDate", endDate);

        editor.apply();

        Toast.makeText(
                this,
                medicineId != -1
                        ? "Medicine updated successfully!"
                        : "Medicine saved successfully!",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    // ==========================================
    // LOAD SELECTED USER'S MEDICINE
    // ==========================================

    private void loadSelectedMedicine() {

        Cursor cursor =
                dbHelper.getAllMedicines(userId);

        try {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                if (id == medicineId) {

                    etMedicineName.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "name"
                                    )
                            )
                    );

                    String dosage = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "dosage"
                            )
                    );

                    String frequency = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "frequency"
                            )
                    );

                    setDosageValue(dosage);
                    setFrequencyValue(frequency);

                    etTime.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "reminder_time"
                                    )
                            )
                    );

                    etStartDate.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "start_date"
                                    )
                            )
                    );

                    etEndDate.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "end_date"
                                    )
                            )
                    );

                    return;
                }
            }

        } finally {
            cursor.close();
        }

        Toast.makeText(
                this,
                "Medicine not found",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    // ==========================================
    // SET DOSAGE DROPDOWN VALUE
    // ==========================================

    private void setDosageValue(String value) {

        if (value == null) {
            value = "";
        }

        for (int i = 1; i < dosageOptions.length - 1; i++) {

            if (dosageOptions[i].equalsIgnoreCase(value)) {

                etDosage.setSelection(i);
                etCustomDosage.setText("");

                return;
            }
        }

        etDosage.setSelection(
                dosageOptions.length - 1
        );

        etCustomDosage.setText(value);
        etCustomDosage.setVisibility(View.VISIBLE);
    }

    // ==========================================
    // SET FREQUENCY DROPDOWN VALUE
    // ==========================================

    private void setFrequencyValue(String value) {

        if (value == null) {
            value = "";
        }

        for (int i = 1; i < frequencyOptions.length - 1; i++) {

            if (frequencyOptions[i].equalsIgnoreCase(value)) {

                etFrequency.setSelection(i);
                etCustomFrequency.setText("");

                return;
            }
        }

        etFrequency.setSelection(
                frequencyOptions.length - 1
        );

        etCustomFrequency.setText(value);
        etCustomFrequency.setVisibility(View.VISIBLE);
    }

    // ==========================================
    // CLEAR FORM FOR NEW MEDICINE
    // ==========================================

    private void clearFields() {

        etMedicineName.setText("");
        etDosage.setSelection(0);
        etFrequency.setSelection(0);

        etCustomDosage.setText("");
        etCustomFrequency.setText("");

        etTime.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
    }
}

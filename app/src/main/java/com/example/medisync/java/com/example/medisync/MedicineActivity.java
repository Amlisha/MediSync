
package com.example.medisync;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicineActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    EditText etMedicineName, etDosage, etFrequency;
    EditText etTime, etStartDate, etEndDate;

    Button btnSaveMedicine;

    SharedPreferences preferences;

    // ID of the medicine being edited.
    // -1 means a new medicine is being added.
    int medicineId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        dbHelper = new DatabaseHelper(this);

        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        etFrequency = findViewById(R.id.etFrequency);
        etTime = findViewById(R.id.etTime);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        preferences = getSharedPreferences(
                "MedicineData", MODE_PRIVATE);

        // Check whether the form was opened using Edit.
        medicineId = getIntent().getIntExtra("medicine_id", -1);

        if (medicineId != -1) {
            loadSelectedMedicine();
            btnSaveMedicine.setText("Update Medicine");
        } else {
            // A new medicine form should start empty.
            clearFields();
            btnSaveMedicine.setText("Save Medicine");
        }

        btnSaveMedicine.setOnClickListener(v -> saveMedicine());
    }

    private void saveMedicine() {

        String name = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String frequency = etFrequency.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty()
                || frequency.isEmpty() || time.isEmpty()) {

            Toast.makeText(this,
                    "Please fill in medicine name, dosage, frequency and time",
                    Toast.LENGTH_LONG).show();
            return;
        }

        boolean saved;

        if (medicineId != -1) {
            // Update the existing medicine.
            saved = dbHelper.updateMedicine(
                    medicineId, name, dosage, frequency,
                    startDate, endDate, time);
        } else {
            // Add a new medicine.
            saved = dbHelper.addMedicine(
                    name, dosage, frequency,
                    startDate, endDate, time);
        }

        if (!saved) {
            Toast.makeText(this,
                    "Unable to save medicine",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Keep the latest form values for existing app features.
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("medicineName", name);
        editor.putString("dosage", dosage);
        editor.putString("frequency", frequency);
        editor.putString("time", time);
        editor.putString("startDate", startDate);
        editor.putString("endDate", endDate);
        editor.apply();

        Toast.makeText(this,
                medicineId != -1
                        ? "Medicine updated successfully!"
                        : "Medicine saved successfully!",
                Toast.LENGTH_SHORT).show();

        // Return to the medicine list.
        finish();
    }

    private void loadSelectedMedicine() {

        Cursor cursor = dbHelper.getAllMedicines();

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                if (id == medicineId) {

                    etMedicineName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("name")));

                    etDosage.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("dosage")));

                    etFrequency.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("frequency")));

                    etTime.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("reminder_time")));

                    etStartDate.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("start_date")));

                    etEndDate.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("end_date")));

                    return;
                }
            }
        } finally {
            cursor.close();
        }

        Toast.makeText(this,
                "Medicine not found",
                Toast.LENGTH_SHORT).show();

        finish();
    }
    private void clearFields() {
        etMedicineName.setText("");
        etDosage.setText("");
        etFrequency.setText("");
        etTime.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
    }
}


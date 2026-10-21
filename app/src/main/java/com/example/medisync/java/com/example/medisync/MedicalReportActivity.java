package com.example.medisync;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

public class MedicalReportActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    EditText etReportName, etDoctorName, etHospitalName;
    EditText etReportDate, etDescription;
    Button btnSaveReport;

    SharedPreferences preferences;

    // -1 means adding a new report.
    int reportId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_report);

        dbHelper = new DatabaseHelper(this);
        etReportName = findViewById(R.id.etReportName);
        etDoctorName = findViewById(R.id.etDoctorName);
        etHospitalName = findViewById(R.id.etHospitalName);
        etReportDate = findViewById(R.id.etReportDate);
        etDescription = findViewById(R.id.etDescription);
        btnSaveReport = findViewById(R.id.btnSaveReport);

        etReportDate.setOnClickListener(v -> showDatePicker());

        preferences = getSharedPreferences(
                "MedicalReportData", MODE_PRIVATE);

        reportId = getIntent().getIntExtra("report_id", -1);

        if (reportId != -1) {
            loadSelectedReport();
            btnSaveReport.setText("Update Report");
        } else {
            clearFields();
            btnSaveReport.setText("Save Report");
        }

        btnSaveReport.setOnClickListener(v -> saveReport());
    }

    private void saveReport() {

        String reportName = etReportName.getText().toString().trim();
        String doctorName = etDoctorName.getText().toString().trim();
        String hospitalName = etHospitalName.getText().toString().trim();
        String date = etReportDate.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (reportName.isEmpty()) {
            Toast.makeText(this,
                    "Please enter the report name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean saved;

        if (reportId != -1) {
            saved = dbHelper.updateMedicalReport(
                    reportId, reportName, doctorName,
                    hospitalName, date, description);
        } else {
            saved = dbHelper.addMedicalReport(
                    reportName, doctorName,
                    hospitalName, date, description);
        }

        if (!saved) {
            Toast.makeText(this,
                    "Unable to save report",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Keep the most recent report for existing app features.
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("reportName", reportName);
        editor.putString("doctorName", doctorName);
        editor.putString("hospitalName", hospitalName);
        editor.putString("date", date);
        editor.putString("description", description);

        editor.apply();

        Toast.makeText(this,
                reportId != -1
                        ? "Medical report updated successfully"
                        : "Medical report saved successfully",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    private void loadSelectedReport() {

        Cursor cursor = dbHelper.getAllMedicalReports();

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                if (id == reportId) {

                    etReportName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("title")));

                    etDoctorName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("doctor_name")));

                    etHospitalName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("hospital_name")));

                    etReportDate.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("report_date")));

                    etDescription.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("notes")));

                    return;
                }
            }
        } finally {
            cursor.close();
        }

        Toast.makeText(this,
                "Medical report not found",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    private void clearFields() {
        etReportName.setText("");
        etDoctorName.setText("");
        etHospitalName.setText("");
        etReportDate.setText("");
        etDescription.setText("");
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // If a date is already entered, use it as the initial date.
        String currentDate = etReportDate.getText().toString().trim();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setLenient(false);

        if (!currentDate.isEmpty()) {
            try {
                Date date = dateFormat.parse(currentDate);

                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException ignored) {
                // Use today's date if the saved date is invalid.
            }
        }

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            String selectedDate = String.format(
                                    Locale.US,
                                    "%02d/%02d/%04d",
                                    dayOfMonth,
                                    month + 1,
                                    year
                            );

                            etReportDate.setText(selectedDate);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.show();
    }

}


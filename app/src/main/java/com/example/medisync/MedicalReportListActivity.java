
package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MedicalReportListActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    // Currently logged-in user's ID
    int userId = -1;

    ListView lvMedicalReports;
    TextView tvEmptyReports;
    Button btnAddReport;
    EditText etSearchReports;

    ArrayList<Integer> reportIds = new ArrayList<>();
    ArrayList<String> reportNames = new ArrayList<>();
    ArrayList<String> doctorNames = new ArrayList<>();
    ArrayList<String> hospitalNames = new ArrayList<>();
    ArrayList<String> reportDates = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_report_list);

        dbHelper = new DatabaseHelper(this);

        // Get currently logged-in username
        SharedPreferences accountPreferences =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPreferences.getString(
                "username", ""
        );

        // Find user's ID in the database
        userId = dbHelper.getUserId(username);

        // Prevent access without a valid login
        if (userId <= 0) {
            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        lvMedicalReports = findViewById(R.id.lvMedicalReports);
        tvEmptyReports = findViewById(R.id.tvEmptyReports);
        btnAddReport = findViewById(R.id.btnAddReport);
        etSearchReports = findViewById(R.id.etSearchReports);

        // Open the form to add a new report
        btnAddReport.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MedicalReportListActivity.this,
                    MedicalReportActivity.class
            );
            startActivity(intent);
        });

        // Search medical reports while typing
        etSearchReports.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start,
                    int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start,
                    int before, int count) {

                loadReports(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh reports when returning from Add or Edit
        if (userId > 0 && etSearchReports != null) {
            loadReports(
                    etSearchReports.getText().toString()
            );
        }
    }

    // ==========================================
    // LOAD ALL REPORTS
    // ==========================================

    private void loadReports() {

        String searchText = etSearchReports == null
                ? ""
                : etSearchReports.getText().toString();

        loadReports(searchText);
    }

    // ==========================================
    // LOAD AND SEARCH USER'S REPORTS
    // ==========================================

    private void loadReports(String searchText) {

        if (userId <= 0) {
            return;
        }

        reportIds.clear();
        reportNames.clear();
        doctorNames.clear();
        hospitalNames.clear();
        reportDates.clear();
        descriptions.clear();

        String query = searchText.trim().toLowerCase(
                Locale.ROOT
        );

        // Get only reports belonging to this user
        Cursor cursor = dbHelper.getAllMedicalReports(userId);

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow("title")
                );

                String doctor = cursor.getString(
                        cursor.getColumnIndexOrThrow("doctor_name")
                );

                String hospital = cursor.getString(
                        cursor.getColumnIndexOrThrow("hospital_name")
                );

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("report_date")
                );

                String notes = cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")
                );

                // Search by report name, doctor or hospital
                boolean matches =
                        (title != null
                                && title.toLowerCase(Locale.ROOT)
                                .contains(query))
                                || (doctor != null
                                && doctor.toLowerCase(Locale.ROOT)
                                .contains(query))
                                || (hospital != null
                                && hospital.toLowerCase(Locale.ROOT)
                                .contains(query));

                if (matches) {

                    reportIds.add(id);
                    reportNames.add(title);
                    doctorNames.add(doctor);
                    hospitalNames.add(hospital);
                    reportDates.add(date);
                    descriptions.add(notes);
                }
            }

        } finally {
            cursor.close();
        }

        // Display reports using the existing item layout
        lvMedicalReports.setAdapter(new ReportAdapter());

        // Show appropriate empty-list message
        if (reportIds.isEmpty()) {

            tvEmptyReports.setText(
                    query.isEmpty()
                            ? "No medical reports saved yet."
                            : "No matching medical reports found."
            );

            tvEmptyReports.setVisibility(View.VISIBLE);
            lvMedicalReports.setVisibility(View.GONE);

        } else {

            tvEmptyReports.setVisibility(View.GONE);
            lvMedicalReports.setVisibility(View.VISIBLE);
        }
    }

    // ==========================================
    // REPORT LIST ADAPTER
    // ==========================================

    private class ReportAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return reportIds.size();
        }

        @Override
        public Object getItem(int position) {
            return reportNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return reportIds.get(position);
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(
                        MedicalReportListActivity.this
                ).inflate(
                        R.layout.item_medical_report,
                        parent,
                        false
                );
            }

            TextView tvDetails =
                    view.findViewById(R.id.tvReportDetails);

            Button btnEdit =
                    view.findViewById(R.id.btnEditReport);

            Button btnDelete =
                    view.findViewById(R.id.btnDeleteReport);

            int reportId = reportIds.get(position);

            String reportName = reportNames.get(position);

            // Display saved report details
            tvDetails.setText(
                    displayValue(reportName) + "\n"
                            + "Doctor: "
                            + displayValue(doctorNames.get(position))
                            + "\n"
                            + "Hospital: "
                            + displayValue(hospitalNames.get(position))
                            + "\n"
                            + "Date: "
                            + displayValue(reportDates.get(position))
                            + "\n"
                            + "Notes: "
                            + displayValue(descriptions.get(position))
            );

            // Edit selected report
            btnEdit.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MedicalReportListActivity.this,
                        MedicalReportActivity.class
                );

                intent.putExtra("report_id", reportId);

                startActivity(intent);
            });

            // Delete selected report
            btnDelete.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        MedicalReportListActivity.this
                )
                        .setTitle("Delete Report")
                        .setMessage(
                                "Are you sure you want to delete "
                                        + reportName + "?"
                        )
                        .setNegativeButton(
                                "Cancel",
                                (dialog, which) -> dialog.dismiss()
                        )
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) -> {

                                    // Delete only if this report
                                    // belongs to the logged-in user
                                    boolean deleted =
                                            dbHelper.deleteMedicalReport(
                                                    userId,
                                                    reportId
                                            );

                                    if (deleted) {

                                        Toast.makeText(
                                                MedicalReportListActivity.this,
                                                "Report deleted successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadReports();

                                    } else {

                                        Toast.makeText(
                                                MedicalReportListActivity.this,
                                                "Could not delete report",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        )
                        .show();
            });

            return view;
        }
    }

    // ==========================================
    // DISPLAY OPTIONAL VALUES
    // ==========================================

    private String displayValue(String value) {

        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value;
    }
}

package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MedicalReportListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    ListView lvMedicalReports;
    TextView tvEmptyReports;
    Button btnAddReport;

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

        lvMedicalReports = findViewById(R.id.lvMedicalReports);
        tvEmptyReports = findViewById(R.id.tvEmptyReports);
        btnAddReport = findViewById(R.id.btnAddReport);

        btnAddReport.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MedicalReportListActivity.this,
                    MedicalReportActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void loadReports() {

        reportIds.clear();
        reportNames.clear();
        doctorNames.clear();
        hospitalNames.clear();
        reportDates.clear();
        descriptions.clear();

        Cursor cursor = dbHelper.getAllMedicalReports();

        try {
            while (cursor.moveToNext()) {

                reportIds.add(cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")));

                reportNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("title")));

                doctorNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("doctor_name")));

                hospitalNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("hospital_name")));

                reportDates.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("report_date")));

                descriptions.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")));
            }
        } finally {
            cursor.close();
        }

        lvMedicalReports.setAdapter(new ReportAdapter());

        if (reportIds.isEmpty()) {
            tvEmptyReports.setVisibility(View.VISIBLE);
            lvMedicalReports.setVisibility(View.GONE);
        } else {
            tvEmptyReports.setVisibility(View.GONE);
            lvMedicalReports.setVisibility(View.VISIBLE);
        }
    }

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
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View view = LayoutInflater.from(
                    MedicalReportListActivity.this).inflate(
                    R.layout.item_medical_report, parent, false);

            TextView tvDetails =
                    view.findViewById(R.id.tvReportDetails);

            Button btnEdit =
                    view.findViewById(R.id.btnEditReport);

            Button btnDelete =
                    view.findViewById(R.id.btnDeleteReport);

            int reportId = reportIds.get(position);

            tvDetails.setText(
                    reportNames.get(position) + "\n" +
                            "Doctor: " + displayValue(doctorNames.get(position)) + "\n" +
                            "Hospital: " + displayValue(hospitalNames.get(position)) + "\n" +
                            "Date: " + displayValue(reportDates.get(position)) + "\n" +
                            "Notes: " + displayValue(descriptions.get(position)));

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MedicalReportListActivity.this,
                        MedicalReportActivity.class);

                intent.putExtra("report_id", reportId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(MedicalReportListActivity.this)
                        .setTitle("Delete Report")
                        .setMessage("Are you sure you want to delete "
                                + reportNames.get(position) + "?")
                        .setNegativeButton("Cancel", (dialog, which) ->
                                dialog.dismiss())
                        .setPositiveButton("Delete", (dialog, which) -> {

                            boolean deleted =
                                    dbHelper.deleteMedicalReport(reportId);

                            if (deleted) {
                                Toast.makeText(
                                        MedicalReportListActivity.this,
                                        "Report deleted successfully",
                                        Toast.LENGTH_SHORT).show();

                                loadReports();
                            } else {
                                Toast.makeText(
                                        MedicalReportListActivity.this,
                                        "Could not delete report",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            });

            return view;
        }
    }

    private String displayValue(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value;
    }
}

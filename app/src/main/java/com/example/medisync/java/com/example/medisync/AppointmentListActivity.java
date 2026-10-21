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

public class AppointmentListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ListView lvAppointments;
    TextView tvEmptyAppointments;
    Button btnAddAppointment;

    ArrayList<Integer> appointmentIds = new ArrayList<>();
    ArrayList<String> doctorNames = new ArrayList<>();
    ArrayList<String> hospitalNames = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        dbHelper = new DatabaseHelper(this);

        lvAppointments = findViewById(R.id.lvAppointments);
        tvEmptyAppointments = findViewById(R.id.tvEmptyAppointments);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        btnAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AppointmentListActivity.this,
                    AppointmentReminderActivity.class);
            startActivity(intent);
        });

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentIds.clear();
        doctorNames.clear();
        hospitalNames.clear();
        dates.clear();
        times.clear();
        descriptions.clear();

        Cursor cursor = dbHelper.getAllAppointments();

        try {
            while (cursor.moveToNext()) {
                appointmentIds.add(cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")));

                doctorNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("doctor_name")));

                hospitalNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("hospital_name")));

                dates.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_date")));

                times.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("appointment_time")));

                descriptions.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")));
            }
        } finally {
            cursor.close();
        }

        boolean isEmpty = appointmentIds.isEmpty();

        tvEmptyAppointments.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE);
        lvAppointments.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE);

        lvAppointments.setAdapter(new AppointmentAdapter());
    }

    private class AppointmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appointmentIds.size();
        }

        @Override
        public Object getItem(int position) {
            return appointmentIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return appointmentIds.get(position);
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = LayoutInflater.from(
                    AppointmentListActivity.this).inflate(
                    R.layout.item_appointment, parent, false);

            TextView tvDetails =
                    row.findViewById(R.id.tvAppointmentDetails);
            Button btnEdit =
                    row.findViewById(R.id.btnEditAppointment);
            Button btnDelete =
                    row.findViewById(R.id.btnDeleteAppointment);

            String details = "Dr. " + doctorNames.get(position)
                    + "\nHospital: " + hospitalNames.get(position)
                    + "\nDate: " + dates.get(position)
                    + "\nTime: " + times.get(position);

            String description = descriptions.get(position);

            if (description != null && !description.trim().isEmpty()) {
                details += "\nNotes: " + description;
            }

            tvDetails.setText(details);

            int appointmentId = appointmentIds.get(position);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(
                        AppointmentListActivity.this,
                        AppointmentReminderActivity.class);

                intent.putExtra("appointment_id", appointmentId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(AppointmentListActivity.this)
                        .setTitle("Delete Appointment")
                        .setMessage("Are you sure you want to delete "
                                + "this appointment with Dr. "
                                + doctorNames.get(position) + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            RemindScheduler.cancelReminder(
                                    AppointmentListActivity.this,
                                    appointmentId
                            );
                            boolean deleted =
                                    dbHelper.deleteAppointment(appointmentId);

                            if (deleted) {
                                Toast.makeText(
                                        AppointmentListActivity.this,
                                        "Appointment deleted",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(
                                        AppointmentListActivity.this,
                                        "Could not delete appointment",
                                        Toast.LENGTH_SHORT).show();
                            }

                            loadAppointments();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return row;
        }
    }
}

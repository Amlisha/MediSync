
package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

    // ID of the currently logged-in user
    int userId = -1;

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

        // ==========================================
        // GET LOGGED-IN USER
        // ==========================================

        SharedPreferences accountPreferences =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPreferences.getString(
                "username", ""
        );

        // Find the user's database ID
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

        // ==========================================
        // INITIALIZE VIEWS
        // ==========================================

        lvAppointments = findViewById(
                R.id.lvAppointments
        );

        tvEmptyAppointments = findViewById(
                R.id.tvEmptyAppointments
        );

        btnAddAppointment = findViewById(
                R.id.btnAddAppointment
        );

        // Open the form to add a new appointment
        btnAddAppointment.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AppointmentListActivity.this,
                    AppointmentReminderActivity.class
            );

            startActivity(intent);
        });

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list after adding or editing appointments
        if (userId > 0 && lvAppointments != null) {
            loadAppointments();
        }
    }

    // ==========================================
    // LOAD LOGGED-IN USER'S APPOINTMENTS
    // ==========================================

    private void loadAppointments() {

        if (userId <= 0) {
            return;
        }

        appointmentIds.clear();
        doctorNames.clear();
        hospitalNames.clear();
        dates.clear();
        times.clear();
        descriptions.clear();

        // Retrieve appointments belonging only to this user
        Cursor cursor =
                dbHelper.getAllAppointments(userId);

        try {

            while (cursor.moveToNext()) {

                appointmentIds.add(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        )
                );

                doctorNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "doctor_name"
                                )
                        )
                );

                hospitalNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "hospital_name"
                                )
                        )
                );

                dates.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointment_date"
                                )
                        )
                );

                times.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointment_time"
                                )
                        )
                );

                descriptions.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "notes"
                                )
                        )
                );
            }

        } finally {
            cursor.close();
        }

        // ==========================================
        // DISPLAY LIST OR EMPTY MESSAGE
        // ==========================================

        boolean isEmpty = appointmentIds.isEmpty();

        tvEmptyAppointments.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        lvAppointments.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );

        lvAppointments.setAdapter(
                new AppointmentAdapter()
        );
    }

    // ==========================================
    // APPOINTMENT LIST ADAPTER
    // ==========================================

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
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                row = LayoutInflater.from(
                        AppointmentListActivity.this
                ).inflate(
                        R.layout.item_appointment,
                        parent,
                        false
                );
            }

            TextView tvDetails =
                    row.findViewById(
                            R.id.tvAppointmentDetails
                    );

            Button btnEdit =
                    row.findViewById(
                            R.id.btnEditAppointment
                    );

            Button btnDelete =
                    row.findViewById(
                            R.id.btnDeleteAppointment
                    );

            // Get details of the selected appointment
            int appointmentId =
                    appointmentIds.get(position);

            String doctorName =
                    doctorNames.get(position);

            String hospitalName =
                    hospitalNames.get(position);

            String date =
                    dates.get(position);

            String time =
                    times.get(position);

            String description =
                    descriptions.get(position);

            // ==========================================
            // DISPLAY APPOINTMENT DETAILS
            // ==========================================

            String details =
                    "Dr. " + displayValue(doctorName)
                            + "\nHospital: "
                            + displayValue(hospitalName)
                            + "\nDate: "
                            + displayValue(date)
                            + "\nTime: "
                            + displayValue(time);

            if (description != null &&
                    !description.trim().isEmpty()) {

                details += "\nNotes: " + description;
            }

            tvDetails.setText(details);

            // ==========================================
            // EDIT APPOINTMENT
            // ==========================================

            btnEdit.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AppointmentListActivity.this,
                        AppointmentReminderActivity.class
                );

                intent.putExtra(
                        "appointment_id",
                        appointmentId
                );

                startActivity(intent);
            });

            // ==========================================
            // DELETE APPOINTMENT
            // ==========================================

            btnDelete.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        AppointmentListActivity.this
                )
                        .setTitle("Delete Appointment")
                        .setMessage(
                                "Are you sure you want to delete "
                                        + "this appointment with Dr. "
                                        + displayValue(doctorName)
                                        + "?"
                        )
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) -> {

                                    // Delete only if the appointment
                                    // belongs to the logged-in user
                                    boolean deleted =
                                            dbHelper.deleteAppointment(
                                                    userId,
                                                    appointmentId
                                            );

                                    if (deleted) {

                                        // Cancel the reminder only after
                                        // the database deletion succeeds
                                        RemindScheduler.cancelReminder(
                                                AppointmentListActivity.this,
                                                appointmentId
                                        );

                                        Toast.makeText(
                                                AppointmentListActivity.this,
                                                "Appointment deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else {

                                        Toast.makeText(
                                                AppointmentListActivity.this,
                                                "Could not delete appointment",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    loadAppointments();
                                }
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .show();
            });

            return row;
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

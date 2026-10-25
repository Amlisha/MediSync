
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

public class MedicineListActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    int userId = -1;

    ListView lvMedicines;
    TextView tvEmptyMedicines;
    Button btnAddMedicine;

    ArrayList<Integer> medicineIds = new ArrayList<>();
    ArrayList<String> medicineNames = new ArrayList<>();
    ArrayList<String> medicineDosages = new ArrayList<>();
    ArrayList<String> medicineFrequencies = new ArrayList<>();
    ArrayList<String> medicineTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        dbHelper = new DatabaseHelper(this);

        // Get currently logged-in user
        SharedPreferences accountPrefs =
                getSharedPreferences(
                        "MediSyncAccount",
                        MODE_PRIVATE
                );

        String username = accountPrefs.getString(
                "username", ""
        );

        userId = dbHelper.getUserId(username);

        // Prevent access without login
        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Initialize views
        lvMedicines = findViewById(R.id.lvMedicines);
        tvEmptyMedicines = findViewById(R.id.tvEmptyMedicines);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        // Add medicine
        btnAddMedicine.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MedicineListActivity.this,
                    MedicineActivity.class
            );

            startActivity(intent);
        });

        loadMedicines();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh after adding or editing medicines
        if (userId > 0 && lvMedicines != null) {
            loadMedicines();
        }
    }

    // ==========================================
    // LOAD USER'S MEDICINES
    // ==========================================

    private void loadMedicines() {

        if (userId <= 0) {
            return;
        }

        medicineIds.clear();
        medicineNames.clear();
        medicineDosages.clear();
        medicineFrequencies.clear();
        medicineTimes.clear();

        // Only retrieve medicines belonging to this user
        Cursor cursor = dbHelper.getAllMedicines(userId);

        try {

            while (cursor.moveToNext()) {

                medicineIds.add(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        )
                );

                medicineNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")
                        )
                );

                medicineDosages.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("dosage")
                        )
                );

                medicineFrequencies.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("frequency")
                        )
                );

                medicineTimes.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("reminder_time")
                        )
                );
            }

        } finally {
            cursor.close();
        }

        lvMedicines.setAdapter(new MedicineAdapter());

        // Show empty message when no medicines exist
        if (medicineIds.isEmpty()) {

            tvEmptyMedicines.setVisibility(View.VISIBLE);
            lvMedicines.setVisibility(View.GONE);

        } else {

            tvEmptyMedicines.setVisibility(View.GONE);
            lvMedicines.setVisibility(View.VISIBLE);
        }
    }

    // ==========================================
    // MEDICINE LIST ADAPTER
    // ==========================================

    private class MedicineAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return medicineIds.size();
        }

        @Override
        public Object getItem(int position) {
            return medicineNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return medicineIds.get(position);
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            View view = convertView;

            if (view == null) {

                view = LayoutInflater.from(
                        MedicineListActivity.this
                ).inflate(
                        R.layout.item_medicine,
                        parent,
                        false
                );
            }

            TextView tvDetails =
                    view.findViewById(R.id.tvMedicineDetails);

            Button btnEdit =
                    view.findViewById(R.id.btnEditMedicine);

            Button btnDelete =
                    view.findViewById(R.id.btnDeleteMedicine);

            int medicineId = medicineIds.get(position);

            String medicineName = medicineNames.get(position);
            String dosage = medicineDosages.get(position);
            String frequency = medicineFrequencies.get(position);
            String time = medicineTimes.get(position);

            // Display medicine details
            tvDetails.setText(
                    medicineName + "\n" +
                            "Dosage: " + dosage + "\n" +
                            "Frequency: " + frequency + "\n" +
                            "Time: " + time
            );

            // ==========================================
            // EDIT MEDICINE
            // ==========================================

            btnEdit.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MedicineListActivity.this,
                        MedicineActivity.class
                );

                intent.putExtra("medicine_id", medicineId);

                startActivity(intent);
            });

            // ==========================================
            // DELETE MEDICINE
            // ==========================================

            btnDelete.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        MedicineListActivity.this
                )
                        .setTitle("Delete Medicine")
                        .setMessage(
                                "Are you sure you want to delete "
                                        + medicineName + "?"
                        )
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) -> {

                                    boolean deleted =
                                            dbHelper.deleteMedicine(
                                                    userId,
                                                    medicineId
                                            );

                                    if (deleted) {

                                        // Update medicine preferences
                                        // after successful deletion
                                        updateMedicinePreferences(
                                                medicineName,
                                                dosage,
                                                frequency,
                                                time
                                        );

                                        Toast.makeText(
                                                MedicineListActivity.this,
                                                "Medicine deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadMedicines();

                                    } else {

                                        Toast.makeText(
                                                MedicineListActivity.this,
                                                "Could not delete medicine",
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
    // UPDATE SAVED MEDICINE PREFERENCES
    // ==========================================

    private void updateMedicinePreferences(
            String deletedName,
            String deletedDosage,
            String deletedFrequency,
            String deletedTime) {

        SharedPreferences preferences =
                getSharedPreferences(
                        "MedicineData_" + userId,
                        MODE_PRIVATE
                );

        String savedName = preferences.getString(
                "medicineName", ""
        );

        String savedDosage = preferences.getString(
                "dosage", ""
        );

        String savedFrequency = preferences.getString(
                "frequency", ""
        );

        String savedTime = preferences.getString(
                "time", ""
        );

        // Check whether the deleted medicine is the one
        // currently saved for the reminder screen
        boolean matches =
                savedName.equals(deletedName)
                        && savedDosage.equals(deletedDosage)
                        && savedFrequency.equals(deletedFrequency)
                        && savedTime.equals(deletedTime);

        if (!matches) {
            return;
        }

        // Clear saved medicine details
        preferences.edit().clear().apply();

        // Clear the saved reminder details
        SharedPreferences reminderPreferences =
                getSharedPreferences(
                        "MedicineReminderData_" + userId,
                        MODE_PRIVATE
                );

        reminderPreferences.edit().clear().apply();

        // Cancel the user's scheduled medicine reminder
        RemindScheduler.cancelReminder(
                this,
                RemindScheduler.getMedicineRequestCode(userId)
        );
    }
}

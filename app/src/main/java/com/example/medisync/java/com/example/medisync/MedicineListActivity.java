package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MedicineListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
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

        lvMedicines = findViewById(R.id.lvMedicines);
        tvEmptyMedicines = findViewById(R.id.tvEmptyMedicines);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        btnAddMedicine.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MedicineListActivity.this, MedicineActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    private void loadMedicines() {
        medicineIds.clear();
        medicineNames.clear();
        medicineDosages.clear();
        medicineFrequencies.clear();
        medicineTimes.clear();

        Cursor cursor = dbHelper.getAllMedicines();

        try {
            while (cursor.moveToNext()) {
                medicineIds.add(cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")));

                medicineNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("name")));

                medicineDosages.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("dosage")));

                medicineFrequencies.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("frequency")));

                medicineTimes.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("reminder_time")));
            }
        } finally {
            cursor.close();
        }

        lvMedicines.setAdapter(new MedicineAdapter());

        if (medicineIds.isEmpty()) {
            tvEmptyMedicines.setVisibility(View.VISIBLE);
            lvMedicines.setVisibility(View.GONE);
        } else {
            tvEmptyMedicines.setVisibility(View.GONE);
            lvMedicines.setVisibility(View.VISIBLE);
        }
    }

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
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View view = LayoutInflater.from(
                    MedicineListActivity.this).inflate(
                    R.layout.item_medicine, parent, false);

            TextView tvDetails =
                    view.findViewById(R.id.tvMedicineDetails);

            Button btnEdit =
                    view.findViewById(R.id.btnEditMedicine);

            Button btnDelete =
                    view.findViewById(R.id.btnDeleteMedicine);

            int medicineId = medicineIds.get(position);

            tvDetails.setText(
                    medicineNames.get(position) + "\n" +
                            "Dosage: " + medicineDosages.get(position) + "\n" +
                            "Frequency: " + medicineFrequencies.get(position) + "\n" +
                            "Time: " + medicineTimes.get(position));

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MedicineListActivity.this, MedicineActivity.class);

                intent.putExtra("medicine_id", medicineId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(MedicineListActivity.this)
                        .setTitle("Delete Medicine")
                        .setMessage("Are you sure you want to delete "
                                + medicineNames.get(position) + "?")
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton("Delete", (dialog, which) -> {
                            boolean deleted =
                                    dbHelper.deleteMedicine(medicineId);

                            if (deleted) {
                                Toast.makeText(
                                        MedicineListActivity.this,
                                        "Medicine deleted",
                                        Toast.LENGTH_SHORT).show();
                                loadMedicines();
                            } else {
                                Toast.makeText(
                                        MedicineListActivity.this,
                                        "Could not delete medicine",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            });

            return view;
        }
    }
}

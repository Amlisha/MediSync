package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        View btnHealthProfile = findViewById(R.id.btnHealthProfile);

        btnHealthProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RecordActivity.this,
                    HealthProfileActivity.class
            );
            startActivity(intent);
        });
        findViewById(R.id.btnMedicine).setOnClickListener(v -> {
            Intent intent = new Intent(
                    RecordActivity.this,
                    MedicineListActivity.class
            );
            startActivity(intent);
        });
        findViewById(R.id.btnMedicalReport).setOnClickListener(v -> {
            Intent intent = new Intent(
                    RecordActivity.this,
                    MedicalReportListActivity.class
            );
            startActivity(intent);
        });
        findViewById(R.id.btnEmergencyContact).setOnClickListener(v->{
            Intent intent = new Intent(
                    RecordActivity.this,
                    EmergencyContactListActivity.class
            );
            startActivity(intent);
        });
        findViewById(R.id.btnAppointmentReminder).setOnClickListener(v -> {
            Intent intent = new Intent(
                    RecordActivity.this,
                    AppointmentListActivity.class
            );
            startActivity(intent);
        });
    }

}

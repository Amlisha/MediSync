package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderActivity extends AppCompatActivity {
    Button btnMedicineReminder, btnAppointmentReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        btnMedicineReminder = findViewById(R.id.btnMedicineReminder);
        btnAppointmentReminder = findViewById(R.id.btnAppointmentReminder);

        btnMedicineReminder.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ReminderActivity.this,
                    MedicineReminderActivity.class
            );
            startActivity(intent);
        });

        btnAppointmentReminder.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ReminderActivity.this,
                    AppointmentReminderActivity.class
            );
            startActivity(intent);
        });
    }
}

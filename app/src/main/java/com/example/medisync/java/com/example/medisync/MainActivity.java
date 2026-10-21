package com.example.medisync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View btnRecord=findViewById(R.id.btnRecord);
        View btnReminders=findViewById(R.id.btnReminders);
        View btnResolve=findViewById(R.id.btnResolve);
        View btnEmergency=findViewById(R.id.btnEmergency);
        View navHome = findViewById(R.id.navHome);
        View navRecords = findViewById(R.id.navRecords);
        View navReminders = findViewById(R.id.navReminders);
        View navAccount = findViewById(R.id.navAccount);
        View btnChatbot = findViewById(R.id.cardMediBot);
        btnRecord.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecordActivity.class
            );
            startActivity(intent);
        });
        btnReminders.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ReminderActivity.class
            );
            startActivity(intent);
        });
        btnResolve.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ResolverActivity.class
            );
            startActivity(intent);
        });
        btnChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ChatbotActivity.class
            );
            startActivity(intent);
        });
        btnEmergency.setOnClickListener(v->{
            android.content.SharedPreferences prefs=getSharedPreferences(
                    "EmergencyContactData",
                    MODE_PRIVATE
            );
            String name = prefs.getString("contactName", "");
            String phone = prefs.getString("phoneNumber", "");

            if (phone.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Please save an emergency contact first",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent = new Intent(
                        MainActivity.this,
                        EmergencyContactActivity.class
                );
                startActivity(intent);

            } else {
                Intent intent = new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phone)
                );
                startActivity(intent);

                Toast.makeText(
                        MainActivity.this,
                        "Emergency contact: " + name,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        // Home: stay on the current screen
        navHome.setOnClickListener(v -> {
            showMessage("You are already on Home");
        });

// Records: open Record Information
        navRecords.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

// Reminders: open the Reminders screen
        navReminders.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
            startActivity(intent);
        });

// Account: open Account screen
        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AccountActivity.class
            );
            startActivity(intent);
        });
        }
    private void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
}

}
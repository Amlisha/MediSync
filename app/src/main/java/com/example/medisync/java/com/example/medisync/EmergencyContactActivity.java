package com.example.medisync;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyContactActivity extends AppCompatActivity {
    EditText etContactName, etRelationship, etPhoneNumber;
    Button btnSaveContact;

    DatabaseHelper dbHelper;
    SharedPreferences preferences;
    int contactId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        etContactName = findViewById(R.id.etContactName);
        etRelationship = findViewById(R.id.etRelationship);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSaveContact = findViewById(R.id.btnSaveContact);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences(
                "EmergencyContactData", MODE_PRIVATE);

        contactId = getIntent().getIntExtra("contact_id", -1);

        if (contactId != -1) {
            loadSelectedContact();
            btnSaveContact.setText("Update Contact");
        } else {
            etContactName.setText("");
            etRelationship.setText("");
            etPhoneNumber.setText("");
            btnSaveContact.setText("Save Contact");
        }

        btnSaveContact.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name = etContactName.getText().toString().trim();
        String relationship =
                etRelationship.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();

        if (name.isEmpty() || relationship.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            Toast.makeText(this, "Enter a valid 10-digit phone number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;

        if (contactId != -1) {
            success = dbHelper.updateEmergencyContact(
                    contactId, name, relationship, phone);
        } else {
            success = dbHelper.addEmergencyContact(
                    name, relationship, phone);
        }

        if (success) {
            // Keep the latest saved contact available to the
            // existing Emergency screen.
            preferences.edit()
                    .putString("contactName", name)
                    .putString("relationship", relationship)
                    .putString("phoneNumber", phone)
                    .apply();

            String message = contactId != -1
                    ? "Emergency contact updated successfully"
                    : "Emergency contact saved successfully";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save contact",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSelectedContact() {
        Cursor cursor = dbHelper.getAllEmergencyContacts();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                if (id == contactId) {
                    etContactName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("name")));

                    etRelationship.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("relationship")));

                    etPhoneNumber.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("phone")));

                    return;
                }
            }
        } finally {
            cursor.close();
        }

        Toast.makeText(this, "Contact not found",
                Toast.LENGTH_SHORT).show();
        finish();
    }
}

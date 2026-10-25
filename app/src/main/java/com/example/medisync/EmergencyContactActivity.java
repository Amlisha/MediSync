
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

    // ID of the currently logged-in user
    int userId = -1;

    // -1 means adding a new emergency contact
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

        // Find the logged-in user's database ID
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

        // Save emergency contact preferences separately
        // for each registered user
        preferences = getSharedPreferences(
                "EmergencyContactData_" + userId,
                MODE_PRIVATE
        );

        // ==========================================
        // ADD OR EDIT CONTACT
        // ==========================================

        contactId = getIntent().getIntExtra(
                "contact_id", -1
        );

        if (contactId != -1) {

            // Load only the selected user's contact
            loadSelectedContact();

            btnSaveContact.setText("Update Contact");

        } else {

            // Clear fields for a new contact
            etContactName.setText("");
            etRelationship.setText("");
            etPhoneNumber.setText("");

            btnSaveContact.setText("Save Contact");
        }

        btnSaveContact.setOnClickListener(
                v -> saveContact()
        );
    }

    // ==========================================
    // SAVE OR UPDATE EMERGENCY CONTACT
    // ==========================================

    private void saveContact() {

        String name = etContactName.getText()
                .toString().trim();

        String relationship = etRelationship.getText()
                .toString().trim();

        String phone = etPhoneNumber.getText()
                .toString().trim();

        // Check whether all fields are filled
        if (name.isEmpty()
                || relationship.isEmpty()
                || phone.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate 10-digit phone number
        if (!phone.matches("[0-9]{10}")) {

            Toast.makeText(
                    this,
                    "Enter a valid 10-digit phone number",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean success;

        if (contactId != -1) {

            // Update only this user's emergency contact
            success = dbHelper.updateEmergencyContact(
                    userId,
                    contactId,
                    name,
                    relationship,
                    phone
            );

        } else {

            // Add a new contact belonging to this user
            success = dbHelper.addEmergencyContact(
                    userId,
                    name,
                    relationship,
                    phone
            );
        }

        if (success) {

            // Keep the latest saved contact available
            // to other screens for this user only
            preferences.edit()
                    .putString("contactName", name)
                    .putString("relationship", relationship)
                    .putString("phoneNumber", phone)
                    .apply();

            String message = contactId != -1
                    ? "Emergency contact updated successfully"
                    : "Emergency contact saved successfully";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Could not save contact",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ==========================================
    // LOAD SELECTED EMERGENCY CONTACT
    // ==========================================

    private void loadSelectedContact() {

        // Get only the logged-in user's contacts
        Cursor cursor =
                dbHelper.getAllEmergencyContacts(userId);

        try {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                if (id == contactId) {

                    etContactName.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "name"
                                    )
                            )
                    );

                    etRelationship.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "relationship"
                                    )
                            )
                    );

                    etPhoneNumber.setText(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "phone"
                                    )
                            )
                    );

                    return;
                }
            }

        } finally {
            cursor.close();
        }

        // Contact does not exist or belongs to another user
        Toast.makeText(
                this,
                "Contact not found",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}

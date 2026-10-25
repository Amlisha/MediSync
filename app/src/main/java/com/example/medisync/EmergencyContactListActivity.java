
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

public class EmergencyContactListActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    // ID of the currently logged-in user
    int userId = -1;

    ListView lvEmergencyContacts;
    TextView tvEmptyContacts;
    Button btnAddContact;

    ArrayList<Integer> contactIds = new ArrayList<>();
    ArrayList<String> contactNames = new ArrayList<>();
    ArrayList<String> relationships = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_list);

        dbHelper = new DatabaseHelper(this);

        // Get the currently logged-in username
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

        lvEmergencyContacts = findViewById(
                R.id.lvEmergencyContacts
        );

        tvEmptyContacts = findViewById(
                R.id.tvEmptyContacts
        );

        btnAddContact = findViewById(
                R.id.btnAddContact
        );

        // Open the form to add a new contact
        btnAddContact.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EmergencyContactListActivity.this,
                    EmergencyContactActivity.class
            );

            startActivity(intent);
        });

        loadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list after adding or editing contacts
        if (userId > 0 && lvEmergencyContacts != null) {
            loadContacts();
        }
    }

    // ==========================================
    // LOAD LOGGED-IN USER'S CONTACTS
    // ==========================================

    private void loadContacts() {

        if (userId <= 0) {
            return;
        }

        contactIds.clear();
        contactNames.clear();
        relationships.clear();
        phoneNumbers.clear();

        // Retrieve contacts belonging only to this user
        Cursor cursor =
                dbHelper.getAllEmergencyContacts(userId);

        try {

            while (cursor.moveToNext()) {

                contactIds.add(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        )
                );

                contactNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        )
                );

                relationships.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "relationship"
                                )
                        )
                );

                phoneNumbers.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "phone"
                                )
                        )
                );
            }

        } finally {
            cursor.close();
        }

        // Show an empty message if no contacts exist
        if (contactIds.isEmpty()) {

            tvEmptyContacts.setVisibility(View.VISIBLE);
            lvEmergencyContacts.setVisibility(View.GONE);

        } else {

            tvEmptyContacts.setVisibility(View.GONE);
            lvEmergencyContacts.setVisibility(View.VISIBLE);
        }

        lvEmergencyContacts.setAdapter(
                new ContactAdapter()
        );
    }

    // ==========================================
    // EMERGENCY CONTACT LIST ADAPTER
    // ==========================================

    private class ContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactIds.size();
        }

        @Override
        public Object getItem(int position) {
            return contactIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return contactIds.get(position);
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                row = LayoutInflater.from(
                        EmergencyContactListActivity.this
                ).inflate(
                        R.layout.item_emergency_contact,
                        parent,
                        false
                );
            }

            TextView tvDetails =
                    row.findViewById(R.id.tvContactDetails);

            Button btnEdit =
                    row.findViewById(R.id.btnEditContact);

            Button btnDelete =
                    row.findViewById(R.id.btnDeleteContact);

            // Get selected contact details
            int contactId = contactIds.get(position);
            String contactName = contactNames.get(position);
            String relationship = relationships.get(position);
            String phone = phoneNumbers.get(position);

            // Display contact details
            tvDetails.setText(
                    "Name: " + displayValue(contactName)
                            + "\nRelationship: "
                            + displayValue(relationship)
                            + "\nPhone: "
                            + displayValue(phone)
            );

            // ==========================================
            // EDIT CONTACT
            // ==========================================

            btnEdit.setOnClickListener(v -> {

                Intent intent = new Intent(
                        EmergencyContactListActivity.this,
                        EmergencyContactActivity.class
                );

                intent.putExtra("contact_id", contactId);

                startActivity(intent);
            });

            // ==========================================
            // DELETE CONTACT
            // ==========================================

            btnDelete.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        EmergencyContactListActivity.this
                )
                        .setTitle("Delete Contact")
                        .setMessage(
                                "Are you sure you want to delete "
                                        + contactName + "?"
                        )
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) -> {

                                    // Delete only the current user's contact
                                    boolean deleted =
                                            dbHelper.deleteEmergencyContact(
                                                    userId,
                                                    contactId
                                            );

                                    if (deleted) {

                                        // Refresh the saved contact
                                        // used by the Emergency button
                                        updateLatestContactPreferences();

                                        Toast.makeText(
                                                EmergencyContactListActivity.this,
                                                "Contact deleted successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else {

                                        Toast.makeText(
                                                EmergencyContactListActivity.this,
                                                "Could not delete contact",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    loadContacts();
                                }
                        )
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return row;
        }
    }

    // ==========================================
    // UPDATE USER'S LATEST SAVED CONTACT
    // ==========================================

    private void updateLatestContactPreferences() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "EmergencyContactData_" + userId,
                        MODE_PRIVATE
                );

        // Get the most recently added remaining contact
        Cursor cursor =
                dbHelper.getAllEmergencyContacts(userId);

        try {

            if (cursor.moveToFirst()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String relationship = cursor.getString(
                        cursor.getColumnIndexOrThrow("relationship")
                );

                String phone = cursor.getString(
                        cursor.getColumnIndexOrThrow("phone")
                );

                preferences.edit()
                        .putString("contactName", name)
                        .putString("relationship", relationship)
                        .putString("phoneNumber", phone)
                        .apply();

            } else {

                // Clear only this user's saved contact preferences
                preferences.edit().clear().apply();
            }

        } finally {
            cursor.close();
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

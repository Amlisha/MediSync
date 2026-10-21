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

public class EmergencyContactListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
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

        lvEmergencyContacts = findViewById(R.id.lvEmergencyContacts);
        tvEmptyContacts = findViewById(R.id.tvEmptyContacts);
        btnAddContact = findViewById(R.id.btnAddContact);

        btnAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(
                    EmergencyContactListActivity.this,
                    EmergencyContactActivity.class);
            startActivity(intent);
        });

        loadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        contactIds.clear();
        contactNames.clear();
        relationships.clear();
        phoneNumbers.clear();

        Cursor cursor = dbHelper.getAllEmergencyContacts();

        try {
            while (cursor.moveToNext()) {
                contactIds.add(cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")));

                contactNames.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("name")));

                relationships.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("relationship")));

                phoneNumbers.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("phone")));
            }
        } finally {
            cursor.close();
        }

        if (contactIds.isEmpty()) {
            tvEmptyContacts.setVisibility(View.VISIBLE);
            lvEmergencyContacts.setVisibility(View.GONE);
        } else {
            tvEmptyContacts.setVisibility(View.GONE);
            lvEmergencyContacts.setVisibility(View.VISIBLE);
        }

        lvEmergencyContacts.setAdapter(new ContactAdapter());
    }

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
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = LayoutInflater.from(
                    EmergencyContactListActivity.this).inflate(
                    R.layout.item_emergency_contact, parent, false);

            TextView tvDetails = row.findViewById(R.id.tvContactDetails);
            Button btnEdit = row.findViewById(R.id.btnEditContact);
            Button btnDelete = row.findViewById(R.id.btnDeleteContact);

            tvDetails.setText(
                    "Name: " + contactNames.get(position) +
                            "\nRelationship: " + relationships.get(position) +
                            "\nPhone: " + phoneNumbers.get(position));

            int contactId = contactIds.get(position);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(
                        EmergencyContactListActivity.this,
                        EmergencyContactActivity.class);

                intent.putExtra("contact_id", contactId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(
                        EmergencyContactListActivity.this)
                        .setTitle("Delete Contact")
                        .setMessage("Are you sure you want to delete "
                                + contactNames.get(position) + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            boolean deleted =
                                    dbHelper.deleteEmergencyContact(contactId);

                            if (deleted) {
                                Toast.makeText(
                                        EmergencyContactListActivity.this,
                                        "Contact deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(
                                        EmergencyContactListActivity.this,
                                        "Could not delete contact",
                                        Toast.LENGTH_SHORT).show();
                            }

                            loadContacts();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return row;
        }
    }
}

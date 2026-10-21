package com.example.medisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="MediSync.db";
    private static final int DATABASE_VERSION=5;
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        //store health profile
        db.execSQL("CREATE TABLE health_profiles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "name TEXT, age INTEGER, blood_group TEXT,"+
                "allergies TEXT,notes TEXT)");
        //store medicine
        db.execSQL("CREATE TABLE medicines(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL, dosage TEXT," +
                "frequency TEXT, start_date TEXT, " +
                "end_date TEXT,reminder_time TEXT)");
        // Store medical report details
        db.execSQL("CREATE TABLE medical_reports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "doctor_name TEXT, " +
                "hospital_name TEXT, " +
                "report_date TEXT, " +
                "notes TEXT)");

        // Store emergency contacts
        db.execSQL("CREATE TABLE emergency_contacts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "relationship TEXT, " +
                "phone TEXT NOT NULL)");

        // Store appointments and their reminders
        db.execSQL("CREATE TABLE appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "doctor_name TEXT, " +
                "hospital_name TEXT, " +
                "appointment_date TEXT, " +
                "appointment_time TEXT, " +
                "notes TEXT)");

        // Store registered users
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE medical_reports ADD COLUMN doctor_name TEXT");
            db.execSQL(
                    "ALTER TABLE medical_reports ADD COLUMN hospital_name TEXT");
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "ALTER TABLE emergency_contacts ADD COLUMN relationship TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE appointments ADD COLUMN doctor_name TEXT");
            db.execSQL("ALTER TABLE appointments ADD COLUMN hospital_name TEXT");
        }
        if (oldVersion < 5) {
            db.execSQL("CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL)");
        }
    }

    // ADD MEDICINE
    public boolean addMedicine(String name, String dosage,
                               String frequency, String startDate,
                               String endDate, String reminderTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("dosage", dosage);
        values.put("frequency", frequency);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("reminder_time", reminderTime);

        long result = db.insert("medicines", null, values);
        return result != -1;
    }

    // GET ALL MEDICINES
    public Cursor getAllMedicines() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "medicines",
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
    }

    // UPDATE MEDICINE
    public boolean updateMedicine(int id, String name,
                                  String dosage, String frequency,
                                  String startDate, String endDate,
                                  String reminderTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("dosage", dosage);
        values.put("frequency", frequency);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("reminder_time", reminderTime);

        int rows = db.update(
                "medicines",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }

    // DELETE MEDICINE
    public boolean deleteMedicine(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                "medicines",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }
    // Add a new medical report
    public boolean addMedicalReport(String title, String doctorName,
                                    String hospitalName, String reportDate,
                                    String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("report_date", reportDate);
        values.put("notes", notes);

        long result = db.insert("medical_reports", null, values);

        return result != -1;
    }


    // Get all saved medical reports
    public Cursor getAllMedicalReports() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "medical_reports",
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
    }


    // Update an existing medical report
    public boolean updateMedicalReport(int id, String title,
                                       String doctorName, String hospitalName,
                                       String reportDate, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("report_date", reportDate);
        values.put("notes", notes);

        int rows = db.update(
                "medical_reports",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }


    // Delete a medical report
    public boolean deleteMedicalReport(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                "medical_reports",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }
    public boolean addEmergencyContact(String name, String relationship, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("relationship", relationship);
        values.put("phone", phone);

        long result = db.insert("emergency_contacts", null, values);
        return result != -1;
    }

    public Cursor getAllEmergencyContacts() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "emergency_contacts",
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
    }

    public boolean updateEmergencyContact(int id, String name,
                                          String relationship, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("relationship", relationship);
        values.put("phone", phone);

        int result = db.update(
                "emergency_contacts",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean deleteEmergencyContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "emergency_contacts",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
    public int addAppointment(String title, String doctorName,
                              String hospitalName, String date,
                              String time, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("appointment_date", date);
        values.put("appointment_time", time);
        values.put("notes", notes);

        return (int) db.insert("appointments", null, values);
    }

    public Cursor getAllAppointments() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "appointments",
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
    }

    public boolean updateAppointment(int id, String title,
                                     String doctorName, String hospitalName,
                                     String date, String time, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("appointment_date", date);
        values.put("appointment_time", time);
        values.put("notes", notes);

        int result = db.update(
                "appointments",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean deleteAppointment(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "appointments",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }


    // Get the saved health profile
    public boolean saveHealthProfile(int age, String bloodGroup,
                                     String allergies, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("age", age);
        values.put("blood_group", bloodGroup);
        values.put("allergies", allergies);
        values.put("notes", notes);

        Cursor cursor = db.query(
                "health_profiles",
                new String[]{"id"},
                null, null, null, null, null
        );

        boolean success;

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);

            success = db.update(
                    "health_profiles",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(id)}
            ) > 0;
        } else {
            success = db.insert(
                    "health_profiles", null, values
            ) != -1;
        }

        cursor.close();
        return success;
    }

    public Cursor getHealthProfile() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "health_profiles",
                null,
                null, null, null, null,
                "id DESC",
                "1"
        );
    }
    // Register a new user
    public boolean registerUser(String name, String username,
                                String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result != -1;
    }
    // Check login details
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }
    // Get registered user's account details
    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "users",
                new String[]{"name", "username"},
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );
    }
    // Change password for a registered user
    public boolean changePassword(String username,
                                  String currentPassword,
                                  String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rows = db.update(
                "users",
                values,
                "username = ? AND password = ?",
                new String[]{username, currentPassword}
        );

        return rows > 0;
    }
}



package com.example.medisync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MediSync.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Store health profile
        db.execSQL("CREATE TABLE health_profiles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name TEXT, age INTEGER, blood_group TEXT, " +
                "allergies TEXT, notes TEXT)");

        // Store medicines
        db.execSQL("CREATE TABLE medicines(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, dosage TEXT, " +
                "frequency TEXT, start_date TEXT, " +
                "end_date TEXT, reminder_time TEXT)");

        // Store medical reports
        db.execSQL("CREATE TABLE medical_reports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "doctor_name TEXT, " +
                "hospital_name TEXT, " +
                "report_date TEXT, " +
                "notes TEXT)");

        // Store emergency contacts
        db.execSQL("CREATE TABLE emergency_contacts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "relationship TEXT, " +
                "phone TEXT NOT NULL)");

        // Store appointments
        db.execSQL("CREATE TABLE appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
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
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE medical_reports " +
                            "ADD COLUMN doctor_name TEXT");

            db.execSQL(
                    "ALTER TABLE medical_reports " +
                            "ADD COLUMN hospital_name TEXT");
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "ALTER TABLE emergency_contacts " +
                            "ADD COLUMN relationship TEXT");
        }

        if (oldVersion < 4) {
            db.execSQL(
                    "ALTER TABLE appointments " +
                            "ADD COLUMN doctor_name TEXT");

            db.execSQL(
                    "ALTER TABLE appointments " +
                            "ADD COLUMN hospital_name TEXT");
        }

        if (oldVersion < 5) {
            db.execSQL("CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL)");
        }

        if (oldVersion < 6) {

            db.execSQL(
                    "ALTER TABLE health_profiles " +
                            "ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1");

            db.execSQL(
                    "ALTER TABLE medicines " +
                            "ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1");

            db.execSQL(
                    "ALTER TABLE medical_reports " +
                            "ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1");

            db.execSQL(
                    "ALTER TABLE emergency_contacts " +
                            "ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1");

            db.execSQL(
                    "ALTER TABLE appointments " +
                            "ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1");
        }
    }

    // ==========================================
    // MEDICINE METHODS
    // ==========================================

    // Add medicine for logged-in user
    public boolean addMedicine(int userId, String name,
                               String dosage, String frequency,
                               String startDate, String endDate,
                               String reminderTime) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("dosage", dosage);
        values.put("frequency", frequency);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("reminder_time", reminderTime);

        long result = db.insert("medicines", null, values);

        return result != -1;
    }

    // Get medicines of logged-in user
    public Cursor getAllMedicines(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "medicines",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC"
        );
    }

    // Update medicine of logged-in user
    public boolean updateMedicine(int userId, int id,
                                  String name, String dosage,
                                  String frequency, String startDate,
                                  String endDate, String reminderTime) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

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
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // Delete medicine of logged-in user
    public boolean deleteMedicine(int userId, int id) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                "medicines",
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // ==========================================
    // MEDICAL REPORT METHODS
    // ==========================================

    // Add medical report for logged-in user
    public boolean addMedicalReport(int userId, String title,
                                    String doctorName,
                                    String hospitalName,
                                    String reportDate, String notes) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("report_date", reportDate);
        values.put("notes", notes);

        long result = db.insert(
                "medical_reports", null, values);

        return result != -1;
    }

    // Get reports of logged-in user
    public Cursor getAllMedicalReports(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "medical_reports",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC"
        );
    }

    // Update report of logged-in user
    public boolean updateMedicalReport(int userId, int id,
                                       String title, String doctorName,
                                       String hospitalName,
                                       String reportDate, String notes) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("report_date", reportDate);
        values.put("notes", notes);

        int rows = db.update(
                "medical_reports",
                values,
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // Delete report of logged-in user
    public boolean deleteMedicalReport(int userId, int id) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                "medical_reports",
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // ==========================================
    // EMERGENCY CONTACT METHODS
    // ==========================================

    // Add emergency contact for logged-in user
    public boolean addEmergencyContact(int userId,
                                       String name,
                                       String relationship,
                                       String phone) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("relationship", relationship);
        values.put("phone", phone);

        long result = db.insert(
                "emergency_contacts", null, values);

        return result != -1;
    }

    // Get emergency contacts of logged-in user
    public Cursor getAllEmergencyContacts(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "emergency_contacts",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC"
        );
    }

    // Update emergency contact of logged-in user
    public boolean updateEmergencyContact(int userId, int id,
                                          String name,
                                          String relationship,
                                          String phone) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("relationship", relationship);
        values.put("phone", phone);

        int rows = db.update(
                "emergency_contacts",
                values,
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // Delete emergency contact of logged-in user
    public boolean deleteEmergencyContact(int userId, int id) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                "emergency_contacts",
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // ==========================================
    // APPOINTMENT METHODS
    // ==========================================

    // Add appointment for logged-in user
    public int addAppointment(int userId, String title,
                              String doctorName,
                              String hospitalName,
                              String date, String time,
                              String notes) {

        if (userId <= 0) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("appointment_date", date);
        values.put("appointment_time", time);
        values.put("notes", notes);

        return (int) db.insert(
                "appointments", null, values);
    }

    // Get appointments of logged-in user
    public Cursor getAllAppointments(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "appointments",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC"
        );
    }

    // Update appointment of logged-in user
    public boolean updateAppointment(int userId, int id,
                                     String title,
                                     String doctorName,
                                     String hospitalName,
                                     String date, String time,
                                     String notes) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("doctor_name", doctorName);
        values.put("hospital_name", hospitalName);
        values.put("appointment_date", date);
        values.put("appointment_time", time);
        values.put("notes", notes);

        int rows = db.update(
                "appointments",
                values,
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // Delete appointment of logged-in user
    public boolean deleteAppointment(int userId, int id) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                "appointments",
                "id = ? AND user_id = ?",
                new String[]{
                        String.valueOf(id),
                        String.valueOf(userId)
                }
        );

        return rows > 0;
    }

    // ==========================================
    // HEALTH PROFILE METHODS
    // ==========================================

    // Save or update logged-in user's health profile
    public boolean saveHealthProfile(int userId, int age,
                                     String bloodGroup,
                                     String allergies,
                                     String notes) {

        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("age", age);
        values.put("blood_group", bloodGroup);
        values.put("allergies", allergies);
        values.put("notes", notes);

        Cursor cursor = db.query(
                "health_profiles",
                new String[]{"id"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC",
                "1"
        );

        try {
            if (cursor.moveToFirst()) {

                int id = cursor.getInt(0);

                int rows = db.update(
                        "health_profiles",
                        values,
                        "id = ? AND user_id = ?",
                        new String[]{
                                String.valueOf(id),
                                String.valueOf(userId)
                        }
                );

                return rows > 0;

            } else {

                long result = db.insert(
                        "health_profiles", null, values);

                return result != -1;
            }

        } finally {
            cursor.close();
        }
    }

    // Get logged-in user's health profile
    public Cursor getHealthProfile(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "health_profiles",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC",
                "1"
        );
    }

    // ==========================================
    // USER ACCOUNT METHODS
    // ==========================================

    // Register a new user
    public boolean registerUser(String name,
                                String username,
                                String password) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result != -1;
    }

    // Check login details
    public boolean checkUser(String username,
                             String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null,
                null,
                null
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    // Get ID of logged-in user
    public int getUserId(String username) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );

        int userId = -1;

        try {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        return userId;
    }

    // Get registered user's account details
    public Cursor getUserDetails(String username) {

        SQLiteDatabase db = getReadableDatabase();

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

    // Change password
    public boolean changePassword(String username,
                                  String currentPassword,
                                  String newPassword) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rows = db.update(
                "users",
                values,
                "username = ? AND password = ?",
                new String[]{
                        username,
                        currentPassword
                }
        );

        return rows > 0;
    }
}

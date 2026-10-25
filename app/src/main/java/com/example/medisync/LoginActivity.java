
package com.example.medisync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;
    CheckBox cbRememberMe;

    DatabaseHelper dbHelper;
    SharedPreferences accountPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountPreferences = getSharedPreferences(
                "MediSyncAccount", MODE_PRIVATE);

        // Skip login only if a valid saved user exists
        String savedUsername = accountPreferences.getString("username", "");

        if (accountPreferences.getBoolean("rememberMe", false)
                && !savedUsername.isEmpty()) {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            if (databaseHelper.getUserId(savedUsername) != -1) {
                openHomePage();
                return;
            }
        }

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean valid = dbHelper.checkUser(username, password);

        if (valid) {

            accountPreferences.edit()
                    .putString("username", username)
                    .putBoolean("rememberMe", cbRememberMe.isChecked())
                    .apply();

            Toast.makeText(this,
                    "Login successful",
                    Toast.LENGTH_SHORT).show();

            openHomePage();

        } else {
            Toast.makeText(this,
                    "Invalid username or password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openHomePage() {
        Intent intent = new Intent(
                LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

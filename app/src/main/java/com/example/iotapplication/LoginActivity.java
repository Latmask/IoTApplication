package com.example.iotapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvRegister;
    private Button bLogin;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.tvMessage);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        bLogin = findViewById(R.id.bLogin);
        DB = new DBHelper(this);

        bLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.equals("") || password.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
            } else {
                Boolean credentialsMatch = DB.checkUsernamePassword(username, password);
                if (credentialsMatch) {
                    Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    User.setName(username);
                    etUsername.setText("");
                    etPassword.setText("");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }
}
package com.example.iotapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.userText);
        password = findViewById(R.id.passwordText);
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(
                view -> validation(
                        username.getText().toString(), password.getText().toString()));
    }

    //TODO hardcoded username = "admin" and password = "password"
    private void validation(String username, String password) {
        if(username.equals("admin") && password.equals("password")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            errorMessage("Wrong username or password.");
        }
    }

    //Error message if the user inputs wrong username/password
    private void errorMessage(String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message)
                .setTitle("Error Message")
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .create()
                .show();
    }
}

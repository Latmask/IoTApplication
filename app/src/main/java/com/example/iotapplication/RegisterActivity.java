package com.example.iotapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etRePassword;
    private Button bRegister;
    private ImageButton ibBackArrow;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.tvMessage);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        ibBackArrow = findViewById(R.id.ibBackArrow);
        DB = new DBHelper((this));
        bRegister = findViewById(R.id.bRegister);

        bRegister.setOnClickListener(view -> {
            try {
                if(registerIsValid()){
                    toastMessageShort("Registered successfully");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        });

        ibBackArrow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private boolean registerIsValid() throws InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchPaddingException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        Boolean userExists = DB.checkUsername(username);
        Boolean userCreated = DB.insertData(username, password);

        if (username.equals("") || password.equals("") || rePassword.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (!password.equals(rePassword)) {
            toastMessageShort("Passwords not matching");
            return false;
        }
        if (userExists) {
            toastMessageShort("User already exists");
            return false;
        }
        if (!userCreated) {
            toastMessageShort("Registration failed");
            return false;
        }
        return true;
    }

    private void toastMessageShort(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
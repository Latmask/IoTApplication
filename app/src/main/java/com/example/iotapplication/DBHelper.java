package com.example.iotapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.crypto.SecretKey;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase iotDB) {
        iotDB.execSQL("CREATE TABLE user(username TEXT, password TEXT, light_data TEXT, lock_data TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase iotDB, int i, int i1) {
        iotDB.execSQL("DROP TABLE IF EXISTS user");
    }

    public Boolean insertLoginData(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Encryption e = new Encryption();
        e.AESEncryptionKeyGenerator(username);
        String encryptedPassword = e.AESEncryptionApplication(password, username);

        contentValues.put("username", username);
        contentValues.put("password", encryptedPassword);
        long result = myDB.insert("user", null, contentValues);

        return result != -1;
    }

    public Boolean updateActuatorData(String username, String colName, String lightData) {
        SQLiteDatabase iotDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, lightData);

        iotDB.update("user", contentValues, "username = ?", new String[] {username} );
        return true;
    }

    public String getLightData(String username) {
        SQLiteDatabase iotDB = this.getReadableDatabase();
        String result = null;
        try (Cursor cursor = iotDB.rawQuery(
                    "SELECT light_data FROM user WHERE username = ?",
                    new String[] {username})) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
            cursor.close();
            return result;
        }
    }

    public String getLockData(String username) {
        SQLiteDatabase iotDB = this.getReadableDatabase();
        String result = null;
        try (Cursor cursor = iotDB.rawQuery(
                "SELECT lock_data FROM user WHERE username = ?",
                new String[] {username})) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
            cursor.close();
            return result;
        }
    }

    public Boolean checkUsername (String username) {
        SQLiteDatabase iotDB = this.getWritableDatabase();
        try (Cursor cursor = iotDB.rawQuery(
                "SELECT * FROM user WHERE username = ?",
                new String[] {username})) {
            return cursor.getCount() > 0;
        }
    }

    public Boolean checkUsernamePassword(String username, String enteredPassword){
        SQLiteDatabase iotDB = this.getWritableDatabase();
        Encryption e = new Encryption();
        try (Cursor cursor = iotDB.rawQuery(
                "SELECT * FROM user WHERE username = ?",
                new String[]{username})) {
            cursor.moveToFirst();
            String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String[] splitter = encryptedPassword.split(" ", 2);
            String correctPassword = e.AESDecryption(splitter[0], splitter[1], username);
            Boolean testPassword = correctPassword.equals(enteredPassword);

            if (testPassword) {
                if (e.CheckIfKeyUsageDepleted(username)) {
                    changePassword(username, enteredPassword);
                }
            }

            cursor.close();
            return testPassword;
        }
    }

    public void changePassword(String username, String enteredPassword){
        SQLiteDatabase iotDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Encryption e = new Encryption();

        e.DeleteKey(username);
        e.AESEncryptionKeyGenerator(username);
        String encryptedPassword = e.AESEncryptionApplication(enteredPassword, username);
        contentValues.put("password", encryptedPassword);
        iotDB.update("user", contentValues, "username = ?", new String[] {username} );

    }

}
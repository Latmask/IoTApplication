package com.example.iotapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public Boolean insertLoginData(String username, String password) throws InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchPaddingException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Encryption e = new Encryption();
        e.AESEncryptionKeyGenerator();
        String encryptedPassword = e.AESEncryptionApplication(password);
        //e.DeleteKey();

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
                new String[]{username/*, password*/})) {
            cursor.moveToFirst();
            String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String[] splitter = encryptedPassword.split(" ", 2);

            String correctPassword = e.AESDecryption(splitter[0], splitter[1]);
            cursor.close();
            return correctPassword.equals(enteredPassword);
            //return cursor.getCount() > 0;
        }
    }
}
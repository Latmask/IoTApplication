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
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("CREATE TABLE user(username TEXT PRIMARY KEY, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int i, int i1) {
        myDB.execSQL("DROP TABLE IF EXISTS user");
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Encryption e = new Encryption();
        e.AESEncryptionKeyGenerator(username);
        String encryptedPassword = e.AESEncryptionApplication(password, username);

        /*String[] usernameArray = new String[]{username};
        myDB.delete("user", "username = ?", usernameArray);*/



        contentValues.put("username", username);
        contentValues.put("password", encryptedPassword);
        long result = myDB.insert("user", null, contentValues);
        return result != -1;
    }

    public Boolean checkUsername (String username) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        try (Cursor cursor = myDB.rawQuery(
                "SELECT * FROM user WHERE username = ?",
                new String[] {username})) {
            return cursor.getCount() > 0;
        }

    }

    public Boolean checkUsernamePassword(String username, String enteredPassword){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Encryption e = new Encryption();
        try (Cursor cursor = myDB.rawQuery(
                "SELECT * FROM user WHERE username = ?",
                new String[]{username})) {
            cursor.moveToFirst();
            String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String[] splitter = encryptedPassword.split(" ", 2);
            String correctPassword = e.AESDecryption(splitter[0], splitter[1], username);
            if(correctPassword.equals(enteredPassword)){
                return true;
            }else{
                return false;
            }
        }
    }
}
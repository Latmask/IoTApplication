package com.example.iotapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";//Name of the database file

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    //Happens first time you create the database(first time you start the application), creates the table used with all the needed columns.
    @Override
    public void onCreate(SQLiteDatabase iotDB) {
        iotDB.execSQL("CREATE TABLE user(username TEXT, password TEXT, light_data TEXT, lock_data TEXT)");
    }

    //If you want to update the database, method needs to be added because class extends SQLiteOpenHelper
    @Override
    public void onUpgrade(SQLiteDatabase iotDB, int i, int i1) {
        iotDB.execSQL("DROP TABLE IF EXISTS user");
        onCreate(iotDB);
    }

    //Creating a new user
    public Boolean insertLoginData(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase(); //Gets the database that we want to write to
        ContentValues contentValues = new ContentValues(); //Used to put values into the database

        Encryption e = new Encryption();
        e.AESEncryptionKeyGenerator(username); //Generates an encryption key
        String encryptedPassword = e.AESEncryptionApplication(password, username); //Applies the encryption key to the password, while tying the password to the username

        contentValues.put("username", username);//Matches the correct key with the correct data, in this case "username" is the key and username is the data
        contentValues.put("password", encryptedPassword); //Inputs the encrypted password
        long result = myDB.insert("user", null, contentValues);//Inbuilt SQLiteDatabase method to insert the data into the database, in this case we insert the new user into a new column

        return result != -1;//If it returns a -1 then the insertion has somehow failed
    }

    //Updates the actuator data, username is which users row that we want to update, colName is is which column we want to update of the user, and the lightData is the data we want to update with, should be named actuatorData
    public Boolean updateActuatorData(String username, String colName, String lightData) {
        SQLiteDatabase iotDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, lightData);

        iotDB.update("user", contentValues, "username = ?", new String[] {username} ); //Inbuilt SQLiteDatabase method update the database with the given data
        return true; //If it goes well we return true, if not we return false
    }

    public String getLightData(String username) {
        SQLiteDatabase iotDB = this.getReadableDatabase();//Gets the database that we want to read from
        String result = null;
        try (Cursor cursor = iotDB.rawQuery( //Cursor is meant to point at a specific row of data from a database
                    "SELECT light_data FROM user WHERE username = ?", //Will return only a single row, because only one username with this name exists in the database.
                    new String[] {username})) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(0); //Points the cursor to the row we want to access and fetches the data
            }
            cursor.close();
            return result; //Return the fetched data
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
        SQLiteDatabase iotDB = this.getWritableDatabase();//Should be getReadableDatabase
        try (Cursor cursor = iotDB.rawQuery(
                "SELECT * FROM user WHERE username = ?", //sends SQL query to check if the user is there
                new String[] {username})) {
            return cursor.getCount() > 0; //If a matching username is found cursor will greater than 0
        }
    }

    public Boolean checkUsernamePassword(String username, String enteredPassword){
        SQLiteDatabase iotDB = this.getWritableDatabase();
        Encryption e = new Encryption();
        try (Cursor cursor = iotDB.rawQuery(
                "SELECT * FROM user WHERE username = ?",
                new String[]{username})) {
            cursor.moveToFirst();

            // return if database is empty
            // first time we run the application the database will be empty,
            // so incase someone tries to log in with an empty database this makes sure the application doesn't do stuff it can't do yet and crash
            if(cursor.getCount() == 0) {
                return false;
            }

            String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password")); //Gets the password from the correct column
            String[] splitter = encryptedPassword.split(" ", 2); //Splits the String because we want to IV value and encryptedPassword as separate values
            String correctPassword = e.AESDecryption(splitter[0], splitter[1], username); //Inputs the IV value, encrypted password and current username
            Boolean testPassword = correctPassword.equals(enteredPassword); //Checks if the correct password was entered

            //Each time decryption is performed cipher usage value will go up by one, so after each time we have used the password we need to check if we have used up all the uses
            //and if we have we need to call the changePasswordEncrpytion method to generate a new key, this has to be done before the application forgots the correct password
            if (e.checkIfKeyUsageDepleted(username)) {
                changePasswordEncryption(username, correctPassword);
            }

            cursor.close();
            return testPassword; //Returns if the password is correct or not
        }catch(CursorIndexOutOfBoundsException ex){
            return false;
        }
    }

    public void changePasswordEncryption(String username, String correctPassword){
        SQLiteDatabase iotDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Encryption e = new Encryption();

        e.DeleteKey(username); //Deletes the key from AndroidKeyStore
        e.AESEncryptionKeyGenerator(username); //Generates a new key with the username
        String encryptedPassword = e.AESEncryptionApplication(correctPassword, username); //Applies the encryption on the entered password
        contentValues.put("password", encryptedPassword);
        iotDB.update("user", contentValues, "username = ?", new String[] {username} ); //Where the username matches we replace the old encrypted password with the new one

    }

}
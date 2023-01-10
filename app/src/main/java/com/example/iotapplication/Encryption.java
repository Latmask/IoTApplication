package com.example.iotapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Encryption {

    public void AESEncryptionKeyGenerator(String username) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");//Makes a keyGenerator of an AES type, and specifices where the password should be stored.
            //"AndroidKeyStore" is secure from external access, so the passwords entirely secure

            keyGenerator.init(
                    new KeyGenParameterSpec.Builder("syncKey" + username, //username is needed to link a specific key to a specific user
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // Does an XOR operation on the earlier data encrypted, with the new plaintext you want to encrypt. Then chains this multiple times, resulting in a stronger encryption.
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7) //Pads the encrypted password with extra data, making it harder to crack because you cannot guess the length
                            .setMaxUsageCount(60) //Amount of times the key can be used, after 60 times it cannot be accessed anymore from AndroidKeyStore
                            .setKeySize(128) //Size of the key, 128 is considered having a good speed and security balance
                            .build()); //Build the key with given properties

            keyGenerator.generateKey(); //Generates the key specified in the builder
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    //Used when we want to encrypt a password
    public String AESEncryptionApplication(String password, String username) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null); // To ensure that it initializes with a value
            SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null); // SecretKey is the keys that we have generated, and username is used to specify which key we want to retrieve

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); //Cipher is used to encrypt or decrypt a value and needs to be instanced with the same parameters as the key its using
            cipher.init(Cipher.ENCRYPT_MODE, syncKey); //Sets the cipher to encrypt mode and gives it the key its supposed to encrypt.
            byte[] IV = cipher.getIV(); //IV value is used to chain the encryption in the block mode and is needed to decrypt the data

            byte[] data = password.getBytes("UTF-8"); //As the encryption is done as bytes we need to convert the password to an array of bytes
            byte[] finalData = cipher.doFinal(data); //Applicates the encryption end ensures that the data is encrypted
            String encryptedPassword = Base64.getEncoder().encodeToString(finalData); //As we want to save the password as a String we want to convert the encrypted password back into a String from bytes
            String iv = Base64.getEncoder().encodeToString(IV); //Also converts the IV value to a String, becuase we later need the IV value to decrypt the data
            return iv + " " + encryptedPassword; //Returns both the IV value and encryptedPassword as a String becuase we need to save both to decrypt the data later

        } catch (IllegalArgumentException | KeyStoreException | UnrecoverableKeyException | NoSuchPaddingException | IllegalBlockSizeException | CertificateException | IOException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String AESDecryption(String iv, String encryptedPassword, String username) {
        byte[] bytes = null;
        byte[] bIv = Base64.getDecoder().decode(iv); //Decodes the IV value to a byte array, which we want's to use to decrypt later

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null); //Gets the correct key for the user to decrypt their password
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, syncKey, new IvParameterSpec(bIv)); //Sets the cipher to decrypt mode and initializes it with the key and IV value it needs to decrypt the password
            byte[] stringBytes = Base64.getDecoder().decode(encryptedPassword); //Decodes the encrypted password from a String to a byte array, as that is what the cipher needs to decrypt
            bytes = cipher.doFinal(stringBytes); //The cipher performs the decryption
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnrecoverableKeyException | CertificateException | KeyStoreException | IOException ex) {
            ex.printStackTrace();
        }

        return new String(bytes, StandardCharsets.UTF_8); //Returns the decrypted value for the password as a String
    }

    public boolean checkIfKeyUsageDepleted(String username) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey testKey = (SecretKey) keyStore.getKey("syncKey" + username, null);
            return testKey == null; //Returns true if testKey is null, which it will be if all uses are used up

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Deletes the key when we need to add a new key with the same username
    public void DeleteKey(String username){
        try{
            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
            keystore.deleteEntry("syncKey" + username);
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}


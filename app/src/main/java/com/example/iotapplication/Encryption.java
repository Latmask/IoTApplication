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
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyGenerator.init(
                    new KeyGenParameterSpec.Builder("syncKey" + username,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setMaxUsageCount(60)
                            .setKeySize(128)
                            .build());

            keyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public String AESEncryptionApplication(String password, String username) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, syncKey);
            byte[] IV = cipher.getIV();

            byte[] data = password.getBytes("UTF-8");
            byte[] finalData = cipher.doFinal(data);
            String encryptedPassword = Base64.getEncoder().encodeToString(finalData);
            String iv = Base64.getEncoder().encodeToString(IV);
            return iv + " " + encryptedPassword;

        } catch (IllegalArgumentException | KeyStoreException | UnrecoverableKeyException | NoSuchPaddingException | IllegalBlockSizeException | CertificateException | IOException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String AESDecryption(String iv, String encryptedPassword, String username) {
        byte[] bytes = null;
        byte[] bIv = Base64.getDecoder().decode(iv);

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, syncKey, new IvParameterSpec(bIv));
            byte[] stringBytes = Base64.getDecoder().decode(encryptedPassword);
            bytes = cipher.doFinal(stringBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnrecoverableKeyException | CertificateException | KeyStoreException | IOException ex) {
            ex.printStackTrace();
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public boolean checkIfKeyUsageDepleted(String username) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey testKey = (SecretKey) keyStore.getKey("syncKey" + username, null);
            return testKey == null;

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

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


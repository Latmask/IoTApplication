package com.example.iotapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Date;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.SecureRandom;

public class Encryption{
    private Date endDate = null;

    public KeyStore GetKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return keyStore;
    }

    public void RSAEncryptionKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
        Date startDate = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        Date endDate = c.getTime();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        "asyncKey",
                        KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                        .setKeyValidityStart(startDate)
                        .setKeyValidityEnd(endDate)
                        /*.setIsStrongBoxBacked(true)*/
                        .build());
        keyPairGenerator.generateKeyPair();
    }
    public void RSAEncryption(String data){
        KeyStore keyStore;
        Signature signature;
        PrivateKey privateKey = null;
        byte[] byteData = data.getBytes(StandardCharsets.UTF_8);

        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            privateKey = (PrivateKey) keyStore.getKey("asyncKey", null);

            signature = Signature.getInstance("SHA256withRSA/PSS");
            signature.initSign(privateKey);
            signature.update(byteData);
            signature.sign();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //PublicKey publicKey = keyStore.getCertificate("asyncKey").getPublicKey();


        /*String object = "example";
        try{
            byte[] signedObject = signature.update(object.toByteArray()).sing();
        }catch(KeyPermanentlyInvalidatedException e){
            //generate new key
        }*/
    }

    public void AESEncryptionKeyGenerator(String username){
        try{
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        keyGenerator.init(
                new KeyGenParameterSpec.Builder("syncKey" + username,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setMaxUsageCount(2)
                        .setKeySize(128)
                        .build());

        keyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    public String AESEncryptionApplication(String password, String username) {
        try{
            KeyStore keyStore =  KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, syncKey);
            byte[] IV = cipher.getIV();

            byte[] data = password.getBytes("UTF-8");
            byte[] finalData = cipher.doFinal(data);
            String encryptedPassword = Base64.getEncoder().encodeToString(finalData);
            String iv = Base64.getEncoder().encodeToString(IV);
            String finalString = iv + " " + encryptedPassword;
            return finalString;

        }catch(IllegalArgumentException | KeyStoreException e){
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String AESDecryption(String iv, String encryptedPassword, String username){
        SecretKey syncKey = null;
        Cipher cipher = null;
        SecretKey testKey = null;
        byte[] bytes = null;
        byte[] bIv = Base64.getDecoder().decode(iv);

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            syncKey = (SecretKey) keyStore.getKey("syncKey" + username, null);
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, syncKey, new IvParameterSpec(bIv));
            byte[] stringBytes = Base64.getDecoder().decode(encryptedPassword);
            bytes = cipher.doFinal(stringBytes);
        }catch(NoSuchAlgorithmException | NoSuchPaddingException ex){
            ex.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String password = new String(bytes, StandardCharsets.UTF_8);
        /*if(CheckIfKeyUsageDepleted(username)){
            DeleteKey(username);
            //AESEncryptionKeyGenerator(username);

        }*/


        return password;
    }

    public boolean CheckIfKeyUsageDepleted(String username){
        SecretKey testKey = null;
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            testKey = (SecretKey) keyStore.getKey("syncKey" + username, null);
            if(testKey == null){
                return true;
            }else{
                return false;
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void TimerSyncKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keystore = KeyStore.getInstance("AndroidKeystore");
        keystore.deleteEntry("syncKey");
        Date startDate = new Date();

        if(endDate == null || startDate.before(endDate)){
           ChangeEndDate();
        }

        if(startDate.equals(endDate)){

        }

    }
    public void ChangeEndDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 2);
        Date endDate = c.getTime();
        this.endDate = endDate;

    }

    public void DeleteKey(String username){
        try{
            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
            keystore.deleteEntry("syncKey" + username);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}


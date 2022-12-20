package com.example.iotapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

public class Encryption implements Serializable {
    private byte[] IV = null;
    private ArrayList<Light> listOfLights;
    private ArrayList<Lock> listOfLocks;
    private Date endDate = null;
    public static void main(String args[]) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, CertificateException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {

       new  Encryption().AESEncryptionKeyGenerator();


        KeyStore keyStore = new Encryption().GetKeyStore();
        SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey", null);

        Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, syncKey);
        Light light1 = new Light("light one", "light 01", 0, false);

        byte [] data = cipher.update(SerializationUtils.serialize((Serializable) light1));
        cipher.doFinal(data);
        for(int i = 0; i > data.length; i++){
            System.out.print(data[i] + " ");
        }
    }

    public KeyStore GetKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return keyStore;
    }

    public void PublicKeyEncryptionKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
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

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Signature signature = Signature.getInstance("SHA256withRSA/PSS");

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("asyncKey", null);
        PublicKey publicKey = keyStore.getCertificate("asyncKey").getPublicKey();
        signature.initSign(privateKey);

        /*String object = "example";
        try{
            byte[] signedObject = signature.update(object.toByteArray()).sing();
        }catch(KeyPermanentlyInvalidatedException e){
            //generate new key
        }*/
    }

    public void AESEncryptionKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        keyGenerator.init(
                new KeyGenParameterSpec.Builder("syncKey",
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        //.setMaxUsageCount(60)
                        //.setKeySize(256)
                        .build());

        keyGenerator.generateKey();

    }
    public String AESEncryptionApplication(String password) throws InvalidKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyStore keyStore =  KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        SecretKey syncKey = (SecretKey) keyStore.getKey("syncKey", null);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, syncKey);
        byte[] IV = cipher.getIV();

        byte[] data = password.getBytes("UTF-8");
        byte[] finalData = cipher.doFinal(data);
        String encryptedPassword = Base64.getEncoder().encodeToString(finalData);
        String iv = Base64.getEncoder().encodeToString(IV);
        String finalString = iv + " " + encryptedPassword;

        return finalString;

    }

    public String AESDecryption(String iv, String encryptedPassword){
        SecretKey syncKey = null;
        Cipher cipher = null;
        byte[] bytes = null;
        byte[] bIv = Base64.getDecoder().decode(iv);
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            syncKey = (SecretKey) keyStore.getKey("syncKey", null);
        }catch(KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException ex){
            ex.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, syncKey, new IvParameterSpec(bIv));
        }catch(NoSuchAlgorithmException | NoSuchPaddingException ex){
            ex.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //cipher.update(encryptedPassword);
        byte[] stringBytes = Base64.getDecoder().decode(encryptedPassword);
        try{
            bytes = cipher.doFinal(stringBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String password = new String(bytes, StandardCharsets.UTF_8);
        return password;
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

    public void DeleteKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
        keystore.load(null);
        keystore.deleteEntry("syncKey");
    }

}


package com.example.iotapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
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
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Calendar;

public class Encryption {


    public void PublicKeyEncryptionGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
        Date startDate = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        Date endDate = c.getTime();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        "key1",
                        KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                        .setKeyValidityStart(startDate)
                        .setKeyValidityEnd(endDate)
                        .build());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Signature signature = Signature.getInstance("SHA256withRSA/PSS");

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("key1", null);
        PublicKey publicKey = keyStore.getCertificate("key1").getPublicKey();
        signature.initSign(privateKey);
    }






}


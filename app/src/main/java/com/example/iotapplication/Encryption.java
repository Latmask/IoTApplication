package com.example.iotapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.util.Date;
import java.util.Calendar;

public class Encryption {


    public void InstanceGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
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
        signature.initSign(keyPair.getPrivate());
    }





}


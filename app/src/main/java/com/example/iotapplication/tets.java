package com.example.iotapplication;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class tets {
    public static void main(String args[]){
        String s = "ab";
        byte[] data = SerializationUtils.serialize((Serializable) s);
        for(int i = 0; i > data.length; i++){
            System.out.print(data[i] + " ");
        }
    }
}
 //   AND password = ?

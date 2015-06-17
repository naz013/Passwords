package com.cray.software.passwords.helpers;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Crypter {

    public Crypter(){

    }

    public String decrypt(String toDecrypt){
        byte[] byte_pass = Base64.decode(toDecrypt, Base64.DEFAULT);
        String decrypted = "";
        try {
            decrypted = new String(byte_pass, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return decrypted;
    }

    public String encrypt(String toEncrypt){
        byte[] passByted = null;
        try {
            passByted = toEncrypt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(passByted, Base64.DEFAULT).trim();
    }
}

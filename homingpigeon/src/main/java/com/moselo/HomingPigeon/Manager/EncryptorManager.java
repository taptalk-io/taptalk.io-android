package com.moselo.HomingPigeon.Manager;

import android.util.Log;

import com.moselo.HomingPigeon.Helper.AESCrypt;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;

import java.security.GeneralSecurityException;

public class EncryptorManager {

    private static EncryptorManager instance;

    public static EncryptorManager getInstance() {
        return instance == null ? (instance = new EncryptorManager()) : instance;
    }

    public EncryptorManager() {

    }

    public String encrypt(String textToEncrypt, String id) throws GeneralSecurityException {
        String localKey, encrypted, encryptedWithSalt;
        char salt;
        int encryptedLength, saltIndex;
        int randomNumber = Utils.getInstance().generateRandomNumber(9);

        try {
            salt = id.charAt(textToEncrypt.length() % 32);
            localKey = new StringBuilder(Utils.getInstance().mySubString(id, 8, 16)).reverse().toString();
            encrypted = AESCrypt.encrypt(String.format("%s%s", DefaultConstant.ENCRYPTION_KEY, localKey), textToEncrypt);
            encryptedLength = encrypted.length();
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encryptedWithSalt = new StringBuilder(encrypted).insert(saltIndex, salt).insert(0, randomNumber).toString();
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return encryptedWithSalt;
    }

    public String decrypt(String textToDecrypt, String id) throws GeneralSecurityException {
        String localKey, encrypted, decrypted;
        int randomNumber, encryptedLength, saltIndex;
        try {
            localKey = new StringBuilder(Utils.getInstance().mySubString(id, 8, 16)).reverse().toString();
            randomNumber = (int) textToDecrypt.charAt(0);
            encryptedLength = textToDecrypt.length() - 2;
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encrypted = new StringBuilder(textToDecrypt).deleteCharAt(0).deleteCharAt(saltIndex).toString();
            decrypted = AESCrypt.decrypt(String.format("%s%s", DefaultConstant.ENCRYPTION_KEY, localKey), encrypted);
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return decrypted;
    }
}

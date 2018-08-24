package com.moselo.HomingPigeon.Manager;

import com.moselo.HomingPigeon.Helper.AESCrypt;

import java.security.GeneralSecurityException;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.EncryptKey;

public class EncryptorManager {

    private static EncryptorManager instance;

    public static EncryptorManager getInstance() {
        return instance == null ? (instance = new EncryptorManager()) : instance;
    }

    public EncryptorManager() {

    }

    public String encrypt(String textToEncrypt) throws GeneralSecurityException {
        return AESCrypt.encrypt(EncryptKey, textToEncrypt);
    }

    public String decrypt(String textToDecrypt) throws GeneralSecurityException {
        return AESCrypt.decrypt(EncryptKey, textToDecrypt);
    }
}

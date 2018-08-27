package com.moselo.HomingPigeon.Manager;

import android.util.Log;

import com.moselo.HomingPigeon.Helper.AESCrypt;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;

import java.security.GeneralSecurityException;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.EncryptionKey;

public class EncryptorManager {

    private static EncryptorManager instance;

    public static EncryptorManager getInstance() {
        return instance == null ? (instance = new EncryptorManager()) : instance;
    }

    public EncryptorManager() {

    }

    public String encrypt(String textToEncrypt, String id) throws GeneralSecurityException {
        String il, im, ir, en, es;
        try {
            il = Utils.mySubString(id, 0, 8);
            im = Utils.mySubString(id, 8, 16);
            ir = Utils.mySubString(id, 24, 8);
            en = AESCrypt.encrypt(String.format("%s%s", DefaultConstant.EncryptionKey, im), textToEncrypt);
            es = String.format("%s%s%s", il, ir, en);
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return es;
    }

    public String decrypt(String textToDecrypt, String id) throws GeneralSecurityException {
        String il, im, ir, de, ds;
        try {
            il = Utils.mySubString(id, 0, 8);
            im = Utils.mySubString(id, 8, 16);
            ir = Utils.mySubString(id, 24, 8);
            ds = textToDecrypt.replace(String.format("%s%s", il, ir), "");
            de = AESCrypt.decrypt(String.format("%s%s", DefaultConstant.EncryptionKey, im), ds);
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return de;
    }
}

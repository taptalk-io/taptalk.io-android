package com.moselo.HomingPigeon.Manager;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moselo.HomingPigeon.Helper.AESCrypt;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsCallback;
import com.moselo.HomingPigeon.Helper.AESCrypto.JsEncryptor;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Listener.HomingPigeonEncryptorListener;

import java.security.GeneralSecurityException;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.EncryptKey;

public class EncryptorManager {

    private static EncryptorManager instance;

    public static EncryptorManager getInstance() {
        if (null == instance) {
            instance =  new EncryptorManager();
        }
        return instance;
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

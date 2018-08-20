package com.moselo.HomingPigeon.Manager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsCallback;
import com.moselo.HomingPigeon.Helper.AESCrypto.JsEncryptor;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Listener.HomingPigeonEncryptorListener;

public class EncryptorManager {

    private static EncryptorManager instance;
    private JsEncryptor jsEncryptor;
    private HomingPigeonEncryptorListener listener;

    public static EncryptorManager getInstance() {
        if (null == instance) {
            instance =  new EncryptorManager();
        }
        return instance;
    }

    public EncryptorManager() {
        jsEncryptor = JsEncryptor.evaluateAllScripts((AppCompatActivity) HomingPigeon.getAppContext());
    }

    public void setListener(HomingPigeonEncryptorListener listener) {
        if (null != this.listener) {
            this.listener = null;
        }
        this.listener = listener;
    }

    public void encrypt(String textToEncrypt, String encryptionKey) {
        jsEncryptor.encrypt(textToEncrypt, encryptionKey, new JsCallback() {
            @Override
            public void onResult(String s) {
                listener.onEncryptResult(s);
            }

            @Override
            public void onError(String s) {
                listener.onError(s);
                Log.e(instance.getClass().getSimpleName(), "onError: " + s);
            }
        });
    }

    public void decrypt(String textToDecrypt, String encryptionKey) {
        jsEncryptor.decrypt(textToDecrypt, encryptionKey, new JsCallback() {
            @Override
            public void onResult(String s) {
                listener.onDecryptResult(s);
            }

            @Override
            public void onError(String s) {
                listener.onError(s);
                Log.e(instance.getClass().getSimpleName(), "onError: " + s);
            }
        });
    }
}

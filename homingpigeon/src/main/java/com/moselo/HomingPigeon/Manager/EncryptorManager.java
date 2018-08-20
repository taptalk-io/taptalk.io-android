package com.moselo.HomingPigeon.Manager;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsCallback;
import com.moselo.HomingPigeon.Helper.AESCrypto.JsEncryptor;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Listener.HomingPigeonEncryptorListener;

public class EncryptorManager {

    private static EncryptorManager instance;
    private JsEncryptor jsEncryptor;
    private Activity activity;
    private HomingPigeonEncryptorListener listener;

    public static EncryptorManager getInstance(Activity activity) {
        if (null == instance) {
            instance =  new EncryptorManager(activity);
        }
        return instance;
    }

    public EncryptorManager(Activity activity) {
        this.activity = activity;
        jsEncryptor = JsEncryptor.evaluateAllScripts(activity);
    }

    public void setListener(HomingPigeonEncryptorListener listener) {
        if (null != this.listener) {
            this.listener = null;
        }
        this.listener = listener;
    }

    public void encrypt(final String textToEncrypt, final String encryptionKey) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void decrypt(final String textToDecrypt, final String encryptionKey) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }
}

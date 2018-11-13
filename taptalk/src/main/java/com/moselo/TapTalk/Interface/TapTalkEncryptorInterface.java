package com.moselo.TapTalk.Interface;

public interface TapTalkEncryptorInterface {

    void onEncryptResult(String encryptedMessage);

    void onDecryptResult(String decryptedMessage);

    void onError(String error);
}

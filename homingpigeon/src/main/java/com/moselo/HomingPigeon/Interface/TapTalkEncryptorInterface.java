package com.moselo.HomingPigeon.Interface;

public interface TapTalkEncryptorInterface {

    void onEncryptResult(String encryptedMessage);

    void onDecryptResult(String decryptedMessage);

    void onError(String error);
}

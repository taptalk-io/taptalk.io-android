package com.moselo.HomingPigeon.Listener;

public interface HomingPigeonEncryptorListener {

    void onEncryptResult(String encryptedMessage);

    void onDecryptResult(String decryptedMessage);

    void onError(String error);
}

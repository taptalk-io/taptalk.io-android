package com.moselo.HomingPigeon.Interface;

public interface HomingPigeonEncryptorInterface {

    void onEncryptResult(String encryptedMessage);

    void onDecryptResult(String decryptedMessage);

    void onError(String error);
}

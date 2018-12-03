package io.taptalk.TapTalk.Manager;

import java.security.GeneralSecurityException;

import io.taptalk.TapTalk.Helper.AESCrypt;
import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ENCRYPTION_KEY;

public class TAPEncryptorManager {

    private static TAPEncryptorManager instance;

    public static TAPEncryptorManager getInstance() {
        return instance == null ? (instance = new TAPEncryptorManager()) : instance;
    }

    public TAPEncryptorManager() {

    }

    public String encrypt(String textToEncrypt, String id) throws GeneralSecurityException {
        String localKey, encrypted, encryptedWithSalt;
        char salt;
        int encryptedLength, saltIndex;
        int randomNumber = TAPUtils.getInstance().generateRandomNumber(9);

        try {
            salt = id.charAt(textToEncrypt.length() % id.length());
            localKey = new StringBuilder(TAPUtils.getInstance().mySubString(id, 8, 16)).reverse().toString();
            encrypted = AESCrypt.encrypt(String.format("%s%s", TAPUtils.getInstance().mySubString(ENCRYPTION_KEY, 0, 16), localKey), textToEncrypt);
            encryptedLength = encrypted.length();
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encryptedWithSalt = new StringBuilder(encrypted).insert(saltIndex, salt).insert(0, randomNumber).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return encryptedWithSalt;
    }

    public String decrypt(String textToDecrypt, String id) throws GeneralSecurityException {
        String localKey, encrypted, decrypted;
        int randomNumber, encryptedLength, saltIndex;
        try {
            localKey = new StringBuilder(TAPUtils.getInstance().mySubString(id, 8, 16)).reverse().toString();
            randomNumber = Integer.parseInt(String.valueOf(textToDecrypt.charAt(0)));
            encryptedLength = textToDecrypt.length() - 2;
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encrypted = new StringBuilder(textToDecrypt).deleteCharAt(0).deleteCharAt(saltIndex).toString();
            decrypted = AESCrypt.decrypt(String.format("%s%s", TAPUtils.getInstance().mySubString(ENCRYPTION_KEY, 0, 16), localKey), encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return decrypted;
    }
}

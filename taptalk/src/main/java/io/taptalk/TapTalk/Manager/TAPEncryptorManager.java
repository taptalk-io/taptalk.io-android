package io.taptalk.TapTalk.Manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.GeneralSecurityException;
import java.util.HashMap;

import io.taptalk.TapTalk.Helper.AESCrypt;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;

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

    public HashMap<String, Object> encryptMessage(TAPMessageModel messageModel) {
        HashMap<String, Object> encryptedMessageMap = TAPUtils.getInstance().toHashMap(messageModel);
        try {
            encryptedMessageMap.put("body", encrypt(messageModel.getBody(), messageModel.getLocalID()));
            if (null != messageModel.getData()) {
                encryptedMessageMap.put("data", encrypt(TAPUtils.getInstance().toJsonString(messageModel.getData()), messageModel.getLocalID()));
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encryptedMessageMap;
    }

    public TAPMessageModel decryptMessage(HashMap<String, Object> messageMap) {
            try {
                messageMap.put("body", decrypt(messageMap.get("body").toString(), messageMap.get("localID").toString()));
                if (null != messageMap.get("data")) {
                    messageMap.put("data", TAPUtils.getInstance().toHashMap(decrypt(messageMap.get("data").toString(), messageMap.get("localID").toString())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return TAPUtils.getInstance().convertObject(messageMap, new TypeReference<TAPMessageModel>() {});
    }
}

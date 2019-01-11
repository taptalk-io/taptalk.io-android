package io.taptalk.TapTalk.Manager;

import android.util.Log;

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

    private final String K_LOCAL_ID = "localID";
    private final String K_BODY = "body";
    private final String K_DATA = "data";
    private final String K_QUOTE = "quote";
    private final String K_CONTENT = "content";

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
            String localID = messageModel.getLocalID();
            // Encrypt message body
            encryptedMessageMap.put(K_BODY, encrypt(messageModel.getBody(), localID));
            if (null != messageModel.getData() && !messageModel.getData().isEmpty()) {
                // Encrypt message data
                try {
                    encryptedMessageMap.put(K_DATA, encrypt(TAPUtils.getInstance().toJsonString(messageModel.getData()), localID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (null != messageModel.getQuote()) {
                // Encrypt quote content
                HashMap<String, Object> quoteMap = TAPUtils.getInstance().toHashMap(messageModel.getQuote());
                quoteMap.put(K_CONTENT, encrypt(messageModel.getQuote().getContent(), localID));
                encryptedMessageMap.put(K_QUOTE, quoteMap);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encryptedMessageMap;
    }

    public TAPMessageModel decryptMessage(HashMap<String, Object> messageMap) {
        try {
            String localID = messageMap.get(K_LOCAL_ID).toString();
            // Decrypt message body
            messageMap.put(K_BODY, decrypt(messageMap.get(K_BODY).toString(), localID));
            if (null != messageMap.get(K_DATA) && !messageMap.get(K_DATA).toString().isEmpty()) {
                // Decrypt message data
                try {
                    messageMap.put(K_DATA, TAPUtils.getInstance().toHashMap(decrypt(messageMap.get(K_DATA).toString(), localID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Data is empty
                messageMap.put(K_DATA, null);
            }
            if (null != messageMap.get(K_QUOTE)) {
                // Decrypt quote content
                HashMap<String, Object> quoteMap = TAPUtils.getInstance().toHashMap(messageMap.get(K_QUOTE));
                quoteMap.put(K_CONTENT, decrypt(quoteMap.get(K_CONTENT).toString(), localID));
                messageMap.put(K_QUOTE, quoteMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TAPUtils.getInstance().convertObject(messageMap, new TypeReference<TAPMessageModel>() {});
    }
}

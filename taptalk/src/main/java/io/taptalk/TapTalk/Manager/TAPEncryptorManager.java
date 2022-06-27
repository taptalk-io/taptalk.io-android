package io.taptalk.TapTalk.Manager;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.taptalk.TapTalk.Helper.AESCrypt;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ENCRYPTION_KEY;

public class TAPEncryptorManager {

    private static final String TAG = TAPEncryptorManager.class.getName();
    private static TAPEncryptorManager instance;

    private final String K_LOCAL_ID = "localID";
    private final String K_BODY = "body";
    private final String K_DATA = "data";
    private final String K_QUOTE = "quote";
    private final String K_CONTENT = "content";
    private final String K_ROOM = "room";
    private final String K_PARTICIPANTS = "participants";
    private final String K_ADMINS = "admins";
    private final String K_LOCKED = "locked";
    private final String K_UNREAD_COUNT = "unreadCount";
    private final String K_USER = "user";

    public static TAPEncryptorManager getInstance() {
        return instance == null ? (instance = new TAPEncryptorManager()) : instance;
    }

    public TAPEncryptorManager() {

    }

    public String encrypt(String textToEncrypt, String id) {
        if (textToEncrypt.isEmpty() || id.isEmpty()) {
            return "";
        }
        String localKey, encrypted, encryptedWithSalt;
        char salt;
        int encryptedLength, saltIndex;
        int randomNumber = TAPUtils.generateRandomNumber(9);

        try {
            salt = id.charAt(textToEncrypt.length() % id.length());
            localKey = new StringBuilder(TAPUtils.mySubString(id, 8, 16)).reverse().toString();
            encrypted = AESCrypt.encrypt(String.format("%s%s", TAPUtils.mySubString(ENCRYPTION_KEY, 0, 16), localKey), textToEncrypt);
            encryptedLength = encrypted.length();
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encryptedWithSalt = new StringBuilder(encrypted).insert(saltIndex, salt).insert(0, randomNumber).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return encryptedWithSalt;
    }

    public String decrypt(String textToDecrypt, String id) {
        if (textToDecrypt.isEmpty() || id.isEmpty()) {
            return "";
        }
        String localKey, encrypted, decrypted;
        int randomNumber, encryptedLength, saltIndex;
        try {
            localKey = new StringBuilder(TAPUtils.mySubString(id, 8, 16)).reverse().toString();
            randomNumber = Integer.parseInt(String.valueOf(textToDecrypt.charAt(0)));
            encryptedLength = textToDecrypt.length() - 2;
            saltIndex = ((encryptedLength + randomNumber) * randomNumber) % encryptedLength;
            encrypted = new StringBuilder(textToDecrypt).deleteCharAt(0).deleteCharAt(saltIndex).toString();
            decrypted = AESCrypt.decrypt(String.format("%s%s", TAPUtils.mySubString(ENCRYPTION_KEY, 0, 16), localKey), encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return decrypted;
    }

    public HashMap<String, Object> encryptMessage(TAPMessageModel messageModel) {
//        HashMap<String, Object> encryptedMessageMap = TAPUtils.toHashMap(messageModel);
        HashMap<String, Object> encryptedMessageMap = messageModel.toHashMap();
        try {
            String localID = messageModel.getLocalID();
            // Encrypt message body
            encryptedMessageMap.put(K_BODY, encrypt(messageModel.getBody(), localID));
            if (null != messageModel.getData() && !messageModel.getData().isEmpty()) {
                // Encrypt message data
                encryptedMessageMap.put(K_DATA, encrypt(TAPUtils.toJsonString(messageModel.getData()), localID));
            } else {
                encryptedMessageMap.put(K_DATA, "");
            }
            if (null != messageModel.getQuote()) {
                // Encrypt quote content
                HashMap<String, Object> quoteMap = TAPUtils.toHashMap(messageModel.getQuote());
                quoteMap.put(K_CONTENT, encrypt(messageModel.getQuote().getContent(), localID));
                encryptedMessageMap.put(K_QUOTE, quoteMap);
            }
            // Remove unused fields for socket emit
            encryptedMessageMap.remove(K_USER);
            HashMap<String, Object> roomMap = (HashMap<String, Object>) encryptedMessageMap.get(K_ROOM);
            if (roomMap != null) {
                roomMap.remove(K_PARTICIPANTS);
                roomMap.remove(K_ADMINS);
                roomMap.remove(K_LOCKED);
                roomMap.remove(K_UNREAD_COUNT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedMessageMap;
    }

    public TAPMessageModel decryptMessage(HashMap<String, Object> messageMap) {
        try {
            String localID = (String) messageMap.get(K_LOCAL_ID);
            // Decrypt message body
            String body = (String) messageMap.get(K_BODY);
            if (null != body && !body.isEmpty()) {
                // Decrypt message body
                messageMap.put(K_BODY, decrypt((String) messageMap.get(K_BODY), localID));
            } else {
                // Body is empty
                messageMap.put(K_BODY, "");
            }
            String data = (String) messageMap.get(K_DATA);
            if (null != data && !data.isEmpty()) {
                // Decrypt message data
                messageMap.put(K_DATA, TAPUtils.toHashMap(decrypt((String) messageMap.get(K_DATA), localID)));
            } else {
                // Data is empty
                messageMap.put(K_DATA, null);
            }
            if (null != messageMap.get(K_QUOTE)) {
                // Decrypt quote content
                HashMap<String, Object> quoteMap = TAPUtils.toHashMap(messageMap.get(K_QUOTE));
                quoteMap.put(K_CONTENT, decrypt((String) quoteMap.get(K_CONTENT), localID));
                messageMap.put(K_QUOTE, quoteMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TAPMessageModel decryptedMessage = TAPMessageModel.fromHashMap(messageMap);
        if (null != decryptedMessage) {
            decryptedMessage.updateMessageStatusText();
        }
        return decryptedMessage;
    }

    public String md5(String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException nae) {
            Log.e(TAG, "md5: ", nae);
        }
        return "";
    }
}

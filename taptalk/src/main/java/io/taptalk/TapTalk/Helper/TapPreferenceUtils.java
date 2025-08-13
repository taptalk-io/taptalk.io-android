package io.taptalk.TapTalk.Helper;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import io.taptalk.TapTalk.Manager.TAPEncryptorManager;

public class TapPreferenceUtils {

    private static final String TAG = TapPreferenceUtils.class.getSimpleName();
    private static TapPreferenceUtils instance;
    private static SharedPreferences sharedPreferences;
    private static HashMap<String, String> encryptedKeys = new HashMap<>();
    private static HashMap<String, Object> decryptedPreferences = new HashMap<>();

    public static TapPreferenceUtils getInstance() {
        if (null == instance) {
            instance = new TapPreferenceUtils();
        }
        return instance;
    }

    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TapTalk.appContext);
        }
        return sharedPreferences;
    }

    private static boolean isContextOrKeyEmpty(String key) {
        return TapTalk.appContext == null || key == null || key.isEmpty();
    }

    private static boolean isSavePreferenceParamsInvalid(String key, Object preferenceData) {
        if (isContextOrKeyEmpty(key)) {
            return true;
        }
        if (preferenceData == null) {
            removePreference(key);
            return true;
        }
        return false;
    }

    private static String getEncryptedKey(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        if (encryptedKeys.containsKey(key)) {
            return encryptedKeys.get(key);
        }
        String appendKey = "TapTalkPreferenceKey" + key + "Appended";
        try {
            String encryptedKey = TAPEncryptorManager.getInstance().simpleEncrypt(appendKey, key);
            encryptedKeys.put(key, encryptedKey);
            return encryptedKey;
        }
        catch (Exception e) {
            Log.e(TAG, "getEncryptedKey: " + e.getMessage());
            return key;
        }
    }

    public static void savePreference(String key, Object preferenceData) {
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        decryptedPreferences.put(key, preferenceData);
        try {
            String dataString = TAPUtils.toJsonString(preferenceData);
            String encrypted = TAPEncryptorManager.getInstance().encrypt(dataString, key);
            getSharedPreferences().edit().putString(getEncryptedKey(key), encrypted).apply();
        }
        catch (Exception e) {
            Log.e(TAG, "savePreference: " + e.getMessage());
        }
    }

    public static void saveStringPreference(String key, String preferenceData) {
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        decryptedPreferences.put(key, preferenceData);
        try {
            String encrypted = TAPEncryptorManager.getInstance().encrypt(preferenceData, key);
            getSharedPreferences().edit().putString(getEncryptedKey(key), encrypted).apply();
        }
        catch (Exception e) {
            Log.e(TAG, "saveStringPreference: " + e.getMessage());
        }
    }

    public static void saveLongPreference(String key, Long preferenceData) {
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        saveStringPreference(key, String.valueOf(preferenceData));
    }

    public static void saveFloatPreference(String key, Float preferenceData) {
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        saveStringPreference(key, String.valueOf(preferenceData));
    }

    public static void saveBooleanPreference(String key, Boolean preferenceData) {
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        decryptedPreferences.put(key, preferenceData);
        try {
            getSharedPreferences().edit().putBoolean(getEncryptedKey(key), preferenceData).apply();
        }
        catch (Exception e) {
            Log.e(TAG, "saveBooleanPreference: " + e.getMessage());
        }
    }

    public static Boolean checkPreferenceKeyAvailable(String key) {
        if (isContextOrKeyEmpty(key)) {
            return false;
        }
        // TODO: Remove when migration is over
        if (Hawk.contains(key)) {
            return true;
        }
        if (decryptedPreferences.containsKey(key)) {
            return true;
        }
        return getSharedPreferences().contains(getEncryptedKey(key));
    }

    private static <T> T getFromSharedPreferences(String key, TypeReference<T> type, T defaultValue) {
        try {
            String encrypted = getSharedPreferences().getString(getEncryptedKey(key), "");
            if (encrypted.isEmpty()) {
                return defaultValue;
            }
            String dataString = TAPEncryptorManager.getInstance().decrypt(encrypted, key);
            T decryptedData = TAPUtils.fromJSON(type, dataString);
            decryptedPreferences.put(key, decryptedData);
            return decryptedData;
        }
        catch (Exception e) {
            Log.e(TAG, "getPreference: " + e.getMessage());
            return defaultValue;
        }
    }

    public static <T> T getPreference(String key, TypeReference<T> type) {
        return getPreference(key, type, null);
    }

    public static <T> T getPreference(String key, TypeReference<T> type, T defaultValue) {
        if (isContextOrKeyEmpty(key)) {
            return defaultValue;
        }
        if (decryptedPreferences.containsKey(key)) {
            try {
                return (T) decryptedPreferences.get(key);
            }
            catch (Exception e) {
                return getFromSharedPreferences(key, type, defaultValue);
            }
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, defaultValue);
        }
        return getFromSharedPreferences(key, type, defaultValue);
    }

    public static String getStringPreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return "";
        }
        if (decryptedPreferences.containsKey(key) && decryptedPreferences.get(key) instanceof String) {
            return (String) decryptedPreferences.get(key);
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, "");
        }
        try {
            String encrypted = getSharedPreferences().getString(getEncryptedKey(key), "");
            if (encrypted.isEmpty()) {
                return "";
            }
            String decryptedData = TAPEncryptorManager.getInstance().decrypt(encrypted, key);
            decryptedPreferences.put(key, decryptedData);
            return decryptedData;
        }
        catch (Exception e) {
            Log.e(TAG, "getStringPreference: " + e.getMessage());
            return "";
        }
    }

    public static Long getLongPreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return 0L;
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, 0L);
        }
        String dataString = getStringPreference(key);
        if (dataString == null || dataString.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(dataString);
    }

    public static Float getFloatPreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return 0f;
        }
        String dataString = getStringPreference(key);
        if (dataString == null || dataString.isEmpty()) {
            return 0f;
        }
        return Float.parseFloat(dataString);
    }

    public static Boolean getBooleanPreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return false;
        }
        if (decryptedPreferences.containsKey(key) && decryptedPreferences.get(key) instanceof Boolean) {
            return (Boolean) decryptedPreferences.get(key);
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, false);
        }
        try {
            Boolean decryptedData = getSharedPreferences().getBoolean(getEncryptedKey(key), false);
            decryptedPreferences.put(key, decryptedData);
            return decryptedData;
        }
        catch (Exception e) {
            Log.e(TAG, "getBooleanPreference: " + e.getMessage());
            return false;
        }
    }

    public static void removePreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return;
        }
        decryptedPreferences.remove(key);
        getSharedPreferences().edit().remove(getEncryptedKey(key)).apply();
    }

    public static void removeAllPreferences() {
        if (TapTalk.appContext == null) {
            return;
        }
        decryptedPreferences.clear();
        getSharedPreferences().edit().clear().apply();
    }
}

package io.taptalk.TapTalk.Helper;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orhanobut.hawk.Hawk;

import io.taptalk.TapTalk.Manager.TAPEncryptorManager;

public class TapPreferenceUtils {

    private static SharedPreferences sharedPreferences;

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
            //Log.e(">>>>>>>", "isSavePreferenceParamsInvalid: return " + key + " - " + TapTalk.appContext);
            return true;
        }
        if (preferenceData == null) {
            //Log.e(">>>>>>>", "isSavePreferenceParamsInvalid: removePreference " + key);
            removePreference(key);
            return true;
        }
        return false;
    }

    private static String getEncryptedKey(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        String appendKey = "TapTalkPreferenceKey" + key + "Appended";
        try {
            String encryptedKey = TAPEncryptorManager.getInstance().simpleEncrypt(appendKey, key);
            //Log.e(">>>>>>>", "getEncryptedKey appendKey: " + appendKey);
            //Log.e(">>>>>>>", "getEncryptedKey encryptedKey: " + encryptedKey);
            return encryptedKey;
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "getEncryptedKey exception: " + e.getMessage());
            return key;
        }
    }

    public static void savePreference(String key, Object preferenceData) {
        //Log.e(">>>>>>>", "savePreference key: " + key);
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        try {
            String dataString = TAPUtils.toJsonString(preferenceData);
            String encrypted = TAPEncryptorManager.getInstance().encrypt(dataString, key);
            getSharedPreferences().edit().putString(getEncryptedKey(key), encrypted).apply();
            //Log.e(">>>>>>>", "savePreference dataString: " + dataString);
            //Log.e(">>>>>>>", "savePreference encrypted: " + encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "savePreference exception: " + e.getMessage());
        }
    }

    public static void saveStringPreference(String key, String preferenceData) {
        //Log.e(">>>>>>>", "saveStringPreference key: " + key);
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        try {
            String encrypted = TAPEncryptorManager.getInstance().encrypt(preferenceData, key);
            getSharedPreferences().edit().putString(getEncryptedKey(key), encrypted).apply();
            //Log.e(">>>>>>>", "saveStringPreference preferenceData: " + preferenceData);
            //Log.e(">>>>>>>", "saveStringPreference encrypted: " + encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "saveStringPreference exception: " + e.getMessage());
        }
    }

    public static void saveLongPreference(String key, Long preferenceData) {
        //Log.e(">>>>>>>", "saveLongPreference key: " + key);
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        saveStringPreference(key, String.valueOf(preferenceData));
    }

    public static void saveFloatPreference(String key, Float preferenceData) {
        //Log.e(">>>>>>>", "saveLongPreference key: " + key);
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        saveStringPreference(key, String.valueOf(preferenceData));
    }

    public static void saveBooleanPreference(String key, Boolean preferenceData) {
        //Log.e(">>>>>>>", "saveBooleanPreference key: " + key);
        if (isSavePreferenceParamsInvalid(key, preferenceData)) {
            return;
        }
        try {
            getSharedPreferences().edit().putBoolean(getEncryptedKey(key), preferenceData).apply();
            //Log.e(">>>>>>>", "saveBooleanPreference preferenceData: " + preferenceData);
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "saveBooleanPreference exception: " + e.getMessage());
        }
    }

    public static Boolean checkPreferenceKeyAvailable(String key) {
        //Log.e(">>>>>>>", "checkPreferenceKeyAvailable key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "checkPreferenceKeyAvailable: return " + key + TapTalk.appContext);
            return false;
        }
        // TODO: Remove when migration is over
        if (Hawk.contains(key)) {
            return true;
        }
        return getSharedPreferences().contains(getEncryptedKey(key));
    }

    public static <T> T getPreference(String key, TypeReference<T> type) {
        return getPreference(key, type, null);
    }

    public static <T> T getPreference(String key, TypeReference<T> type, T defaultValue) {
        //Log.e(">>>>>>>", "getPreference key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "getPreference: return " + key + TapTalk.appContext);
            return defaultValue;
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, defaultValue);
        }
        try {
            String encrypted = getSharedPreferences().getString(getEncryptedKey(key), "");
            //Log.e(">>>>>>>", "getPreference encrypted: " + encrypted);
            if (encrypted.isEmpty()) {
                return defaultValue;
            }
            String dataString = TAPEncryptorManager.getInstance().decrypt(encrypted, key);
            //Log.e(">>>>>>>", "getPreference dataString: " + dataString);
            return TAPUtils.fromJSON(type, dataString);
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "getPreference exception: " + e.getMessage());
            return defaultValue;
        }
    }

    public static String getStringPreference(String key) {
        //Log.e(">>>>>>>", "getStringPreference key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "getStringPreference: return " + key + TapTalk.appContext);
            return "";
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, "");
        }
        try {
            String encrypted = getSharedPreferences().getString(getEncryptedKey(key), "");
            //Log.e(">>>>>>>", "getStringPreference encrypted: " + encrypted);
            if (encrypted.isEmpty()) {
                return "";
            }
            String preferenceData = TAPEncryptorManager.getInstance().decrypt(encrypted, key);
            //Log.e(">>>>>>>", "getStringPreference preferenceData: " + preferenceData);
            return preferenceData;
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "getStringPreference exception: " + e.getMessage());
            return "";
        }
    }

    public static Long getLongPreference(String key) {
        //Log.e(">>>>>>>", "getLongPreference key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "getLongPreference: return " + key + TapTalk.appContext);
            return 0L;
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, 0L);
        }
        String dataString = getStringPreference(key);
        //Log.e(">>>>>>>", "getLongPreference dataString: " + dataString);
        if (dataString == null || dataString.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(dataString);
    }

    public static Float getFloatPreference(String key) {
        //Log.e(">>>>>>>", "getFloatPreference key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "getFloatPreference: return " + key + TapTalk.appContext);
            return 0f;
        }
        String dataString = getStringPreference(key);
        //Log.e(">>>>>>>", "getFloatPreference dataString: " + dataString);
        if (dataString == null || dataString.isEmpty()) {
            return 0f;
        }
        return Float.parseFloat(dataString);
    }

    public static Boolean getBooleanPreference(String key) {
        //Log.e(">>>>>>>", "getBooleanPreference key: " + key);
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "getBooleanPreference: return " + key + TapTalk.appContext);
            return false;
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, false);
        }
        try {
            boolean preferenceData = getSharedPreferences().getBoolean(getEncryptedKey(key), false);
            //Log.e(">>>>>>>", "getBooleanPreference preferenceData: " + preferenceData);
            return preferenceData;
        }
        catch (Exception e) {
            e.printStackTrace();
            //Log.e(">>>>>>>", "getBooleanPreference exception: " + e.getMessage());
            return false;
        }
    }

    public static void removePreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            //Log.e(">>>>>>>", "removePreference: return " + TapTalk.appContext);
            return;
        }
        //Log.e(">>>>>>>", "removePreference: " + key);
        getSharedPreferences().edit().remove(getEncryptedKey(key)).apply();
    }

    public static void removeAllPreferences() {
        if (TapTalk.appContext == null) {
            //Log.e(">>>>>>>", "removeAllPreferences: return ");
            return;
        }
        //Log.e(">>>>>>>", "removeAllPreferences: ");
        getSharedPreferences().edit().clear().apply();
    }
}

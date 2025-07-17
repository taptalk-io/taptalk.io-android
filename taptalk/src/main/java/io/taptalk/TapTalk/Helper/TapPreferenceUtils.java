package io.taptalk.TapTalk.Helper;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orhanobut.hawk.Hawk;

import io.taptalk.TapTalk.Manager.TAPEncryptorManager;

public class TapPreferenceUtils {

    private static final String TAG = TapPreferenceUtils.class.getSimpleName();
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
        String appendKey = "TapTalkPreferenceKey" + key + "Appended";
        try {
            return TAPEncryptorManager.getInstance().simpleEncrypt(appendKey, key);
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
        return getSharedPreferences().contains(getEncryptedKey(key));
    }

    public static <T> T getPreference(String key, TypeReference<T> type) {
        return getPreference(key, type, null);
    }

    public static <T> T getPreference(String key, TypeReference<T> type, T defaultValue) {
        if (isContextOrKeyEmpty(key)) {
            return defaultValue;
        }
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, defaultValue);
        }
        try {
            String encrypted = getSharedPreferences().getString(getEncryptedKey(key), "");
            if (encrypted.isEmpty()) {
                return defaultValue;
            }
            String dataString = TAPEncryptorManager.getInstance().decrypt(encrypted, key);
            return TAPUtils.fromJSON(type, dataString);
        }
        catch (Exception e) {
            Log.e(TAG, "getPreference: " + e.getMessage());
            return defaultValue;
        }
    }

    public static String getStringPreference(String key) {
        if (isContextOrKeyEmpty(key)) {
            return "";
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
            return TAPEncryptorManager.getInstance().decrypt(encrypted, key);
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
        // TODO: Remove when migration is over
        if (!checkPreferenceKeyAvailable(key) && Hawk.contains(key)) {
            return Hawk.get(key, false);
        }
        try {
            return getSharedPreferences().getBoolean(getEncryptedKey(key), false);
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
        getSharedPreferences().edit().remove(getEncryptedKey(key)).apply();
    }

    public static void removeAllPreferences() {
        if (TapTalk.appContext == null) {
            return;
        }
        getSharedPreferences().edit().clear().apply();
    }
}

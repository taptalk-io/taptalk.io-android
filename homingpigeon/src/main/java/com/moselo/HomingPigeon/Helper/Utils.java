package com.moselo.HomingPigeon.Helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.R;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

public class Utils {

    private static Utils instance;
    private ObjectMapper objectMapper;

    public static Utils getInstance() {
        return instance == null ? (instance = new Utils()) : instance;
    }


    public Utils() {
        objectMapper = new ObjectMapper();
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public JSONObject toJsonObject(Object object) {
        try {
            return new JSONObject(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        try {
            return objectMapper.readValue(jsonPacket, type);
        } catch (Exception e) {
            Log.e(Utils.class.getSimpleName(), "fromJSON: ",e );
            return null;
        }
    }

    /**
     * generate random string with given length
     *
     * @param length length for string to generate
     * @return generated string
     */
    public String generateRandomString(int length) {
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * generate random number for temporary user ID
     */
    public int generateRandomNumber(int randomNum) {
        Random rnd = new Random();
        return rnd.nextInt(randomNum) + 1;
    }

    /**
     * substring for chat above char limit
     */
    public String mySubString(String myString, int start, int length) {
        return myString.substring(start, Math.min(start + length, myString.length()));
    }

    /**
     * converts Dp into Px
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = HomingPigeon.appContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreeHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * generate random color
     */
    public int getRandomColor(String s) {
        int[] randomColors = HomingPigeon.appContext.getResources().getIntArray(R.array.pastel_colors);
        int hash = 7;
        for (int i = 0, len = s.length(); i < len; i++) {
            hash = s.codePointAt(i) + (hash << 5) - hash;
        }
        int index = Math.abs(hash % randomColors.length);
        return randomColors[index];
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}

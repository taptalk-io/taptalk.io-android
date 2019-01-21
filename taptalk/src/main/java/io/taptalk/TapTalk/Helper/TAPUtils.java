package io.taptalk.TapTalk.Helper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_TYPING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;

public class TAPUtils {

    private static final String TAG = TAPUtils.class.getSimpleName();
    private static TAPUtils instance;
    private ObjectMapper objectMapper;

    public static TAPUtils getInstance() {
        return instance == null ? (instance = new TAPUtils()) : instance;
    }


    public TAPUtils() {
        objectMapper = new ObjectMapper();
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e("><><><", "toJsonString: ", e);
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
            Log.e(TAPUtils.class.getSimpleName(), "fromJSON: ", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Object> toHashMap(Object object) {
        try {
            return objectMapper.convertValue(object, HashMap.class);
        } catch (Exception e) {
            Log.e(TAG, "toHashMap: ", e);
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> toHashMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T convertObject(Object fromObject, TypeReference<T> toObjectType) {
        return objectMapper.convertValue(fromObject, toObjectType);
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
        DisplayMetrics displayMetrics = TapTalk.appContext.getResources().getDisplayMetrics();
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

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * generate random color
     */
    public int getRandomColor(String s) {
        int[] randomColors = TapTalk.appContext.getResources().getIntArray(R.array.pastel_colors);
        int hash = 7;
        for (int i = 0, len = s.length(); i < len; i++) {
            hash = s.codePointAt(i) + (hash << 5) - hash;
        }
        int index = Math.abs(hash % randomColors.length);
        return randomColors[index];
    }

    /**
     * dismiss Keyboard
     */
    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    /**
     * Show Keyboard
     */
    public void showKeyboard(Activity activity, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * separate contact list by initial
     */
    public List<List<TAPUserModel>> separateContactsByInitial(List<TAPUserModel> contacts) {
        List<List<TAPUserModel>> separatedContacts = new ArrayList<>();
        List<TAPUserModel> nonAlphabeticContacts = new ArrayList<>();
        int previousInitialIndexStart = 0;
        int size = contacts.size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    contacts.get(i).getName().charAt(0) !=
                            contacts.get(i - 1).getName().charAt(0)) {
                List<TAPUserModel> contactSubList = contacts.subList(previousInitialIndexStart, i);
                if (Character.isAlphabetic(contactSubList.get(0).getName().charAt(0))) {
                    separatedContacts.add(contactSubList);
                } else {
                    nonAlphabeticContacts.addAll(contactSubList);
                }
                previousInitialIndexStart = i;
            }
        }
        if (!nonAlphabeticContacts.isEmpty()) separatedContacts.add(nonAlphabeticContacts);
        return separatedContacts;
    }

    public String getStringFromURL(URL url) throws IOException {
        StringBuilder fullString = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        url.openStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null)
            fullString.append(inputLine);

        in.close();
        return fullString.toString();
    }

    public String formatCurrencyRp(long value) {
        String str = String.format(Locale.getDefault(), "%,d", value);
        return "Rp " + str.replace(",", ".");
    }


    public void startChatActivity(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        startChatActivity(context, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), false);
    }

    // Open chat room from room list to pass typing status
    public void startChatActivity(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor, boolean isTyping) {
        startChatActivity(context, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), isTyping);
    }

    // Open chat room from notification
    public void startChatActivity(Context context, TAPRoomModel roomModel) {
        startChatActivity(context, roomModel, false);
    }

    private void startChatActivity(Context context, TAPRoomModel roomModel, boolean isTyping) {
        TAPChatManager.getInstance().saveUnsentMessage();
        Intent intent = new Intent(context, TAPChatActivity.class);
        intent.putExtra(K_ROOM, roomModel);
        intent.putExtra(IS_TYPING, isTyping);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    public void pickImageFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        // Reminder: Handle onRequestPermissionsResult in activity
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent = new Intent();
            intent.setType(activity.getString(R.string.intent_pick_image));
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.intent_select_picture)), requestCode);
            }
        }
    }

    /**
     * @return Uri to receive saved image path
     */
    public Uri takePicture(Activity activity, int requestCode) {
        // Reminder: Handle onRequestPermissionsResult in activity using the returned Uri
        if (!hasPermissions(activity, Manifest.permission.CAMERA)) {
            // Check camera permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        } else if (!hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Check write storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA);
        } else {
            // All permissions granted
            Uri imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, requestCode);
            }
            return imageUri;
        }
        return null;
    }

    public ArrayList<TAPImagePreviewModel> getUrisFromClipData(ClipData clipData, ArrayList<TAPImagePreviewModel> uris, boolean isFirstSelected) {
        int itemSize = clipData.getItemCount();
        for (int count = 0; count < itemSize; count++) {
            if (count == 0 && isFirstSelected)
                uris.add(TAPImagePreviewModel.Builder(clipData.getItemAt(count).getUri(), true));
            else uris.add(TAPImagePreviewModel.Builder(clipData.getItemAt(count).getUri(), false));
        }
        return uris;
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public void convertActivityToTranslucentBeforeL(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (Throwable t) {
        }
    }

    /**
     * Ini buat munculin dialog kalau ga ada internet
     */
    public void showNoInternetErrorDialog(Context context) {
        // TODO: 31/10/18 ini textnya masih dummy
        new TapTalkDialog.Builder(context)
                .setTitle("Error")
                .setMessage(context.getString(R.string.no_internet_show_error))
                .setPrimaryButtonTitle("OK")
                .setPrimaryButtonListener(v -> {
                }).show();
    }

    /**
     * untuk ngatur margin view
     */
    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    /*
TODO mengconvert Bitmap menjadi file dikarenakan retrofit hanya mengenali tipe file untuk upload gambarnya sekaligus mengcompressnya menjadi WEBP dikarenakan size bisa sangat kecil dan kualitasnya pun setara dengan PNG.
*/
    public File createTempFile(String mimeType, Bitmap bitmap) {
        File file = new File(TapTalk.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() +"."+mimeType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.WEBP,100, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Ini Untuk Search Position MessageModel dari Sebuah List berdasarkan LocalID
     *
     */
    public int searchMessagePositionByLocalID(List<TAPMessageModel> messageModels, String localID) {
        for (int index = 0; index < messageModels.size(); index++) {
            if (localID.equals(messageModels.get(index).getLocalID())) {
                return index;
            }
        }
        return -1;
    }
}
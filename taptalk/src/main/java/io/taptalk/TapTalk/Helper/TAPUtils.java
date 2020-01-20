package io.taptalk.TapTalk.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity;
import io.taptalk.TapTalk.Helper.CustomTabLayout.TAPCustomTabActivityHelper;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPMapActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.TapTalk.View.Activity.TAPWebBrowserActivity;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.Taptalk.R;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_TYPING_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.JUMP_TO_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TAPUtils {

    private static final String TAG = TAPUtils.class.getSimpleName();
    private static TAPUtils instance;
    private ObjectMapper objectMapper;

    public static TAPUtils getInstance() {
        return instance == null ? (instance = new TAPUtils()) : instance;
    }


    public TAPUtils() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "toJsonString: ", e);
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
            Log.e(TAG, "fromJSON: ", e);
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
            return objectMapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T convertObject(Object fromObject, TypeReference<T> toObjectType) {
        try {
            return objectMapper.convertValue(fromObject, toObjectType);
        } catch (Exception e) {
            Log.e(TAG, "convertObject: ", e);
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
        DisplayMetrics displayMetrics = TapTalk.appContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(Resources res, float dp) {
        return (int) (dp * res.getDisplayMetrics().density);
    }

    /**
     * Converts sp to px
     *
     * @param res Resources
     * @param sp  the value in sp
     * @return int
     */
    public int spToPx(Resources res, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
    }

    public boolean isRtl(Resources res) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && res.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
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

    public int getRandomColor(String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        int[] randomColors = TapTalk.appContext.getResources().getIntArray(R.array.tapDefaultRoomAvatarBackgroundColors);
        int index = (((int) s.charAt(0)) + (int) s.charAt(s.length() - 1) + s.length()) % randomColors.length;
        return randomColors[index];
    }

    public String getInitials(String s, int maxLength) {
        if (null == s || s.length() == 0) {
            return "";
        }
        String initials = s.replaceAll("([^\\s])[^\\s]+", "$1").replaceAll("\\s", "");
        if (initials.length() > maxLength) {
            return initials.substring(0, maxLength);
        }
        return initials;
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

    public void dismissKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    public void showKeyboard(Activity activity, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public List<TapContactListModel> generateContactListForRecycler(List<TAPUserModel> contacts, int type) {
        return generateContactListForRecycler(contacts, type, null);
    }

    public List<TapContactListModel> generateContactListForRecycler(List<TAPUserModel> contacts, int type, @Nullable Map<String, TapContactListModel> contactListPointer) {
        List<TapContactListModel> separatedContacts = new ArrayList<>();
        List<TapContactListModel> nonAlphabeticContacts = new ArrayList<>();
        List<TapContactListModel> filteredContacts = new ArrayList<>();
        for (TAPUserModel contact : contacts) {
            if (null != contact.getUsername() && !contact.getUsername().isEmpty() &&
                    null != contact.getName() && !contact.getName().isEmpty()) {
                TapContactListModel filteredContact = new TapContactListModel(contact, type);
                filteredContacts.add(filteredContact);
            }
        }
        int previousInitialIndexStart = 0;
        int size = filteredContacts.size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    filteredContacts.get(i).getUser().getName().toLowerCase().charAt(0) !=
                            filteredContacts.get(i - 1).getUser().getName().toLowerCase().charAt(0)) {
                List<TapContactListModel> contactSubList = filteredContacts.subList(previousInitialIndexStart, i);
                char initial = contactSubList.get(0).getUser().getName().toLowerCase().charAt(0);
                if ((initial >= 'a' && initial <= 'z') || (initial >= 'A' && initial <= 'Z')) { // Character.isAlphabetic not available below API 19
                //if (Character.isAlphabetic(contactSubList.get(0).getUser().getName().toLowerCase().charAt(0))) {
                    separatedContacts.add(new TapContactListModel(filteredContacts.get(i - 1).getUser().getName().substring(0, 1)));
                    separatedContacts.addAll(contactSubList);
                } else {
                    nonAlphabeticContacts.addAll(contactSubList);
                }
                previousInitialIndexStart = i;
            }
            if (null != contactListPointer) {
                contactListPointer.put(filteredContacts.get(i - 1).getUser().getUsername(), filteredContacts.get(i - 1));
            }
        }
        if (!nonAlphabeticContacts.isEmpty()) {
            separatedContacts.add(new TapContactListModel("#"));
            separatedContacts.addAll(nonAlphabeticContacts);
        }
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

    public String formatThousandSeperator(String value) {
        try {
            Long v = Long.valueOf(value);
            String str = String.format(Locale.getDefault(), "%,d", v);
            return str.replace(",", ".");
        } catch (Exception e) {
            return "0";
        }
    }

    public enum ClipType {TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

    public void clipToRoundedRectangle(View view, int cornerRadius, ClipType clipType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int left = 0;
                    int top = 0;
                    int right = view.getWidth();
                    int bottom = view.getHeight();
                    switch (clipType) {
                        case TOP:
                            bottom += cornerRadius;
                            break;
                        case BOTTOM:
                            top -= cornerRadius;
                            break;
                        case LEFT:
                            right += cornerRadius;
                            break;
                        case RIGHT:
                            left -= cornerRadius;
                            break;
                        case TOP_LEFT:
                            bottom += cornerRadius;
                            right += cornerRadius;
                            break;
                        case TOP_RIGHT:
                            bottom += cornerRadius;
                            left -= cornerRadius;
                            break;
                        case BOTTOM_LEFT:
                            top -= cornerRadius;
                            right += cornerRadius;
                            break;
                        case BOTTOM_RIGHT:
                            top -= cornerRadius;
                            left -= cornerRadius;
                            break;
                    }
                    outline.setRoundRect(left, top, right, bottom, cornerRadius);
                }
            });
            view.setClipToOutline(true);
        }
    }

    public void startChatActivity(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        startChatActivity(context, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, null);
    }

    public void startChatActivity(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor, String jumpToMessageLocalID) {
        startChatActivity(context, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, jumpToMessageLocalID);
    }

    // Open chat room from notification
    public void startChatActivity(Context context, TAPRoomModel roomModel) {
        startChatActivity(context, roomModel, null, null);
    }

    // Open chat room from notification
    public void startChatActivity(Context context, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser) {
        startChatActivity(context, roomModel, typingUser, null);
    }

    public void startChatActivity(Context context, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser, @Nullable String jumpToMessageLocalID) {
        if (TYPE_PERSONAL == roomModel.getRoomType() && TAPChatManager.getInstance().getActiveUser().getUserID().equals(
                TAPChatManager.getInstance().getOtherUserIdFromRoom(roomModel.getRoomID()))) {
            return;
        }

        Activity activity = (Activity) context;
        activity.runOnUiThread(() -> dismissKeyboard(activity));
        TAPChatManager.getInstance().saveUnsentMessage();
        Intent intent = new Intent(context, TapUIChatActivity.class);
        intent.putExtra(ROOM, roomModel);

        if (null != typingUser) {
            Gson gson = new Gson();
            String list = gson.toJson(typingUser);
            intent.putExtra(GROUP_TYPING_MAP, list);
        }
        if (null != jumpToMessageLocalID) {
            intent.putExtra(JUMP_TO_MESSAGE, jumpToMessageLocalID);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            activity.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    public void openProfileActivity(Context context, TAPRoomModel room) {
        Intent intent = new Intent(context, TAPChatProfileActivity.class);
        intent.putExtra(ROOM, room);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    public void openLocationPicker(Activity activity) {
        if (!hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            Intent intent = new Intent(activity, TAPMapActivity.class);
            activity.startActivityForResult(intent, PICK_LOCATION);
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    public void pickImageFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.setType(activity.getString(R.string.tap_intent_type_image));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.tap_intent_title_select_picture)), requestCode);
            }
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    public void pickMediaFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {activity.getString(R.string.tap_intent_type_image), activity.getString(R.string.tap_intent_type_video)};
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Filter only images and videos
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.setType(activity.getString(R.string.tap_intent_type_all));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.tap_intent_title_gallery)), requestCode);
            }
        }
    }

    /**
     * @return Uri to receive saved image path
     * Reminder: Handle onRequestPermissionsResult in activity using the returned Uri
     */
    public Uri takePicture(Activity activity, int requestCode) {
        if (!hasPermissions(activity, Manifest.permission.CAMERA)) {
            // Check camera permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        } else if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA);
        } else {
            // All permissions granted
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                try {
                    String filename = TAPTimeFormatter.getInstance().formatTime(System.currentTimeMillis(), "yyyyMMdd_HHmmssSSS");
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File image = File.createTempFile(filename, ".jpeg", dir);
                    Uri imageUri = FileProvider.getUriForFile(activity, FILEPROVIDER_AUTHORITY, image);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    }
                    activity.startActivityForResult(intent, requestCode);
                    // Save file path to map
                    TAPFileDownloadManager.getInstance().addFileProviderPath(imageUri, image.getAbsolutePath());
                    return imageUri;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public boolean openFile(Context context, Uri uri, String mimeType) {
        String path = TAPFileDownloadManager.getInstance().getFileProviderPath(uri);
        if (null == path) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        context.grantUriPermission(context.getPackageName(), uri, FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType)
                .addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.tap_intent_title_open_file)));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.tap_error_no_app_to_open_file), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void openVideoPreview(Context context, Uri uri) {
        openVideoPreview(context, uri, null);
    }

    public void openVideoPreview(Context context, Uri uri, @Nullable TAPMessageModel message) {
        Intent intent = new Intent(context, TAPVideoPlayerActivity.class);
        intent.putExtra(URI, uri.toString());
        if (null != message) {
            intent.putExtra(MESSAGE, message);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }
    }

    /**
     * Current and maxDuration are in milliseconds
     */
    public String getMediaDurationString(int current, int maxDuration) {
        int secondMs = 1000;
        int minuteMs = 1000 * 60;
        int hourMs = 1000 * 60 * 60;

        String minute = String.valueOf((current % hourMs) / minuteMs);
        if (minute.length() < 2) {
            minute = String.format("0%s", minute);
        }

        String second = current == 0 ? "00" : String.valueOf((current % minuteMs) / secondMs);
        if (second.length() < 2) {
            second = String.format("0%s", second);
        }

        if (maxDuration > hourMs) {
            String hour = String.valueOf(current / hourMs);
            if (hour.length() < 2) {
                hour = String.format("0%s", hour);
            }

            return String.format("%s:%s:%s", hour, minute, second);
        } else {
            return String.format("%s:%s", minute, second);
        }
    }

    public String getMediaDurationStringDummy(int maxDuration) {
        int hourMs = 1000 * 60 * 60;

        if (maxDuration > hourMs) {
            return "00-00-0";
        } else {
            return "00-00";
        }
    }

    public String getFileDisplayName(TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        String fileName = (String) data.get(FILE_NAME);

        if (null != fileName && fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } else if (null != fileName) {
            return fileName;
        } else {
            return "";
        }
    }

    public String getFileDisplayInfo(TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        String fileName = (String) data.get(FILE_NAME);
        String mediaType = (String) data.get(MEDIA_TYPE);
        Number size = (Number) data.get(SIZE);

        String displaySize = "", displayExtension = "";
        if (null != size) {
            displaySize = getStringSizeLengthFile(size.longValue());
        }
        if (null != fileName && fileName.contains(".") && null != size) {
            displayExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        } else if (null != fileName && null != mediaType && mediaType.contains("/")) {
            displayExtension = mediaType.substring(mediaType.lastIndexOf('/') + 1);
        } else if (null != mediaType) {
            displayExtension = mediaType;
        }
        return String.format("%s %s", displaySize, displayExtension).toUpperCase();
    }

    public String getFileDisplayProgress(TAPMessageModel message, Long progressBytes) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        Number size = (Number) data.get(SIZE);
        if (null != size) {
            return String.format("%s / %s", getStringSizeLengthFile(progressBytes), getStringSizeLengthFile(size.longValue()));
        } else {
            return getStringSizeLengthFile(progressBytes);
        }
    }

    public String getFileDisplayDummyInfo(TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        String fileDisplayInfo = getFileDisplayInfo(message);
        Number size = (Number) data.get(SIZE);

        if (null != size) {
            String dummyProgress = String.format(TapTalk.appContext.getString(R.string.tap_file_info_progress_dummy), getStringSizeLengthFile(size.longValue()));
            if (dummyProgress.length() > fileDisplayInfo.length()) {
                return dummyProgress;
            }
        }
        return fileDisplayInfo;
    }

    public ArrayList<TAPMediaPreviewModel> getPreviewsFromClipData(Context context, ClipData clipData, boolean isFirstSelected) {
        ArrayList<TAPMediaPreviewModel> uris = new ArrayList<>();
        int itemSize = clipData.getItemCount();
        for (int count = 0; count < itemSize; count++) {
            Uri uri = clipData.getItemAt(count).getUri();
            if (count == 0 && isFirstSelected) {
                uris.add(TAPMediaPreviewModel.Builder(uri, getMessageTypeFromFileUri(context, uri), true));
            } else {
                uris.add(TAPMediaPreviewModel.Builder(uri, getMessageTypeFromFileUri(context, uri), false));
            }
        }
        return uris;
    }

    public int getMessageTypeFromFileUri(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        // Add message types here
        if (null != type && type.contains("image")) {
            return TYPE_IMAGE;
        } else if (null != type && type.contains("video")) {
            return TYPE_VIDEO;
        } else {
            return -1;
        }
    }

    public void getUserFromXcUserID(String xcUserID, TAPDatabaseListener<TAPUserModel> listener) {
        // Get user from Contact Manager
        TAPDataManager.getInstance().getUserWithXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel entity) {
                if (null != entity) {
                    TAPContactManager.getInstance().updateUserData(entity);
                    listener.onSelectFinished(entity);
                } else {
                    // Get user data from API
                    if (TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
                        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetUserResponse response) {
                                TAPUserModel userResponse = response.getUser();
                                TAPContactManager.getInstance().updateUserData(userResponse);
                                listener.onSelectFinished(userResponse);
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.onSelectFailed(error.getMessage());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                if (TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
                                    TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, this);
                                } else {
                                    listener.onSelectFailed(TapTalk.appContext.getString(R.string.tap_error_open_room_failed));
                                }
                            }
                        });
                    } else {
                        listener.onSelectFailed(TapTalk.appContext.getString(R.string.tap_error_open_room_failed));
                    }
                }
            }
        });
    }

    public int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
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

    public void showNoInternetErrorDialog(Context context) {
        new TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(context.getString(R.string.tap_error))
                .setMessage(context.getString(R.string.tap_no_internet_show_error))
                .setPrimaryButtonTitle(context.getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                }).show();
    }

    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    /**
     * Convert Bitmap to File for Retrofit
     */
    public File createTempFile(String mimeType, Bitmap bitmap) {
        File file = new File(TapTalk.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "." + mimeType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
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

    public int searchMessagePositionByLocalID(List<TAPMessageModel> messageModels, String localID) {
        for (int index = 0; index < messageModels.size(); index++) {
            if (localID.equals(messageModels.get(index).getLocalID())) {
                return index;
            }
        }
        return -1;
    }

    public void openCustomTabLayout(Activity activity, String url) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(activity, R.color.tapColorPrimary));
        intentBuilder.setShowTitle(true);
        intentBuilder.setStartAnimations(activity, R.anim.tap_slide_left, R.anim.tap_stay);
        intentBuilder.setExitAnimations(activity, R.anim.tap_stay,
                R.anim.tap_slide_right);

        TAPCustomTabActivityHelper.openCustomTab(activity,
                intentBuilder.build(), Uri.parse(url), (activity1, uri) -> {
                    Intent intent = new Intent(activity1, TAPWebBrowserActivity.class);
                    intent.putExtra(TAPWebBrowserActivity.EXTRA_URL, uri.toString());
                    activity1.startActivity(intent);
                });
    }

    public void composeEmail(Activity activity, String emailRecipient) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(emailRecipient));
        activity.startActivity(Intent.createChooser(intent, "Send mail"));
    }

    public void openDialNumber(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneNumber));
        activity.startActivity(intent);
    }

    public void composeSMS(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("address", phoneNumber);
        activity.startActivity(intent);
    }

    public void openMaps(Activity activity, Double latitude, Double longitude) {
        Uri googleMapUrl = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude + "&z=16");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapUrl);
        activity.startActivity(mapIntent);
    }

    public void openDocumentPicker(Activity activity) {
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_FILE);
        } else {
            // Permission granted
            Intent intent = new Intent(activity, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.ARG_HIDDEN, true);
            activity.startActivityForResult(intent, SEND_FILE);
            activity.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    public boolean isListEmpty(List t) {
        return null == t || 0 >= t.size();
    }

    public String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.##");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb) {
            return df.format(size / sizeKb) + " KB";
        } else if (size < sizeGb) {
            return df.format(size / sizeMb) + " MB";
        } else if (size < sizeTerra) {
            return df.format(size / sizeGb) + " GB";
        }
        return size + "B";
    }

    public String getFileExtension(File file) {
        if (null != file) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public String getFileMimeType(File file) {
        return URLConnection.guessContentTypeFromName(file.getName());
    }

    public String getImageMimeType(Context context, Uri imageUri) {
        if (null != imageUri && null != imageUri.getScheme() && imageUri.getScheme().contains("content")) {
            return context.getContentResolver().getType(imageUri);
        } else if (null != imageUri) {
            return TAPUtils.getInstance().getFileMimeType(new File(imageUri.toString()));
        } else {
            return IMAGE_JPEG;
        }
    }

    public String getMimeTypeFromUrl(String url) {
        try {
            String type = null;
            String extension = url.substring(url.lastIndexOf(".") + 1);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void rotateAnimateInfinitely(Context context, View view) {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.tap_rotation_infinite);
        rotation.setFillAfter(true);
        view.startAnimation(rotation);
    }

    public void stopViewAnimation(View view) {
        view.clearAnimation();
    }


    public void animateClickButton(View view, Float resize) {
        view.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            } else if (MotionEvent.ACTION_DOWN == event.getAction()) {
                view.animate().scaleX(resize).scaleY(resize).setDuration(100).start();
            } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }
            return false;
        });
    }

    public String getDeviceCountryCode(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();
            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();
        }

        // if network country not available (tablets maybe), get country code from Locale class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }

        if (countryCode != null && countryCode.length() == 2)
            return countryCode.toLowerCase();

        // general fallback to "us"
        return "id";
    }

    @SuppressLint("PrivateApi")
    private String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330:
                    return "PR";
                case 310:
                    return "US";
                case 311:
                    return "US";
                case 312:
                    return "US";
                case 316:
                    return "US";
                case 283:
                    return "AM";
                case 460:
                    return "CN";
                case 455:
                    return "MO";
                case 414:
                    return "MM";
                case 619:
                    return "SL";
                case 450:
                    return "KR";
                case 634:
                    return "SD";
                case 434:
                    return "UZ";
                case 232:
                    return "AT";
                case 204:
                    return "NL";
                case 262:
                    return "DE";
                case 247:
                    return "LV";
                case 255:
                    return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }

    public boolean listEqualsIgnoreOrder(List<TAPUserModel> list1, List<TAPUserModel> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public String getFirstWordOfString(String text) {
        if (text.contains(" ")) {
            return text.substring(0, text.indexOf(' '));
        } else {
            return text;
        }
    }

    public String removeNonAlphaNumeric(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * @return key String to get file message Uri from TapDownloadManager
     */
    public String getUriKeyFromMessage(TAPMessageModel message) {
        if (null == message.getData()) {
            return "";
        }
        String fileUrl = (String) message.getData().get(FILE_URL);
        String fileID = (String) message.getData().get(FILE_ID);
        return null != fileUrl ? TAPUtils.getInstance().removeNonAlphaNumeric(fileUrl).toLowerCase() : fileID;
    }
}

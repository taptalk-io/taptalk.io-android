package io.taptalk.TapTalk.Helper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import io.taptalk.TapTalk.API.Api.TAPApiConnection;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity;
import io.taptalk.TapTalk.Helper.CustomTabLayout.TAPCustomTabActivityHelper;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatActivity;
import io.taptalk.TapTalk.View.Activity.TAPMapActivity;
import io.taptalk.TapTalk.View.Activity.TAPProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.TapTalk.View.Activity.TAPWebBrowserActivity;
import io.taptalk.Taptalk.R;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_TYPING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
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

public class TAPUtils {

    private static final String TAG = TAPUtils.class.getSimpleName();
    private static TAPUtils instance;
    private ObjectMapper objectMapper;

    public static TAPUtils getInstance() {
        return instance == null ? (instance = new TAPUtils()) : instance;
    }


    public TAPUtils() {
        objectMapper = TAPApiConnection.getInstance().createObjectMapper();
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
        List<TAPUserModel> filteredContacts = new ArrayList<>();
        for (TAPUserModel contact : contacts) {
            if (null != contact.getName() && !contact.getName().isEmpty()) {
                filteredContacts.add(contact);
            }
        }
        int previousInitialIndexStart = 0;
        int size = filteredContacts.size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    filteredContacts.get(i).getName().charAt(0) !=
                            filteredContacts.get(i - 1).getName().charAt(0)) {
                List<TAPUserModel> contactSubList = filteredContacts.subList(previousInitialIndexStart, i);
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

    public enum ClipType {TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

    public void clipToRoundedRectangle(View view, int cornerRadius, ClipType clipType) {
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
        Activity activity = (Activity) context;

        activity.runOnUiThread(() -> dismissKeyboard(activity));
        TAPChatManager.getInstance().saveUnsentMessage();
        Intent intent = new Intent(context, TAPChatActivity.class);
        intent.putExtra(ROOM, roomModel);
        intent.putExtra(IS_TYPING, isTyping);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            activity.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    public void openProfileActivity(Context context, TAPRoomModel room) {
        Intent intent = new Intent(context, TAPProfileActivity.class);
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
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(activity.getString(R.string.tap_intent_type_image));
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.tap_intent_title_select_picture)), requestCode);
            }
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    // TODO: 21 March 2019 GET VIDEO FROM GOOGLE DRIVE
    public void pickMediaFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (!hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(activity.getString(R.string.tap_intent_type_all));
            String[] mimeTypes = {activity.getString(R.string.tap_intent_type_image), activity.getString(R.string.tap_intent_type_video)};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Filter only images and videos
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
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
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    activity.startActivityForResult(intent, requestCode);
                    // Save file path to map
                    TAPFileUploadManager.getInstance().addFileProviderPath(imageUri, image.getAbsolutePath());
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
        // TODO: 8 March 2019 CHECK IF FILE EXISTS
        context.grantUriPermission(context.getPackageName(), uri, FLAG_GRANT_READ_URI_PERMISSION);
        Log.e(TAG, "openFile: " + uri);
        Log.e(TAG, "openFile: " + mimeType);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType)
                .addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.tap_intent_title_open_file)));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.tap_error_no_app_to_open_file), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Preview video with external app
    public void openVideoPreview(Context context, Uri uri) {
        Intent intent = new Intent(context, TAPVideoPlayerActivity.class);
        intent.putExtra(URI, uri.toString());
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

        String second = current == 0? "00" : String.valueOf(Math.round(((float) current % minuteMs) / secondMs));
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

    public ArrayList<TAPMediaPreviewModel> getUrisFromClipData(Context context, ClipData clipData, boolean isFirstSelected) {
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
        Log.e(TAG, "getMessageTypeFromFileUri: " + type);
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
                    Log.e(TAG, "getUserFromXcUserID onDbSelectFinished: " + TAPUtils.getInstance().toJsonString(entity));
                    TAPContactManager.getInstance().updateUserDataMap(entity);
                    listener.onSelectFinished(entity);
                } else {
                    // Get user data from API
                    if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
                        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TapDefaultDataView<TAPGetUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetUserResponse response) {
                                TAPUserModel userResponse = response.getUser();
                                Log.e(TAG, "getUserFromXcUserID onApiSuccess: " + TAPUtils.getInstance().toJsonString(userResponse));
                                TAPContactManager.getInstance().updateUserDataMap(userResponse);
                                listener.onSelectFinished(userResponse);
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.onSelectFailed(error.getMessage());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
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
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(context.getString(R.string.tap_error))
                .setMessage(context.getString(R.string.tap_no_internet_show_error))
                .setPrimaryButtonTitle(context.getString(R.string.tap_ok))
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

    /**
     * Ini Untuk Search Position MessageModel dari Sebuah List berdasarkan LocalID
     */
    public int searchMessagePositionByLocalID(List<TAPMessageModel> messageModels, String localID) {
        for (int index = 0; index < messageModels.size(); index++) {
            if (localID.equals(messageModels.get(index).getLocalID())) {
                return index;
            }
        }
        return -1;
    }

    /**
     * This is a function to open Custom Tab Layout
     */
    public void openCustomTabLayout(Activity activity, String url) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(activity.getResources().getColor(R.color.tap_purply_two));
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
        }
    }

    public boolean isListEmpty(List t) {
        return null == t || 0 >= t.size();
    }

    /**
     * Buat ngubah file length jadi format kb/mb/gb
     *
     * @param size = file.length
     * @return
     */
    public String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " GB";

        return size + "B";
    }

    /**
     * Untuk Dapetin file Extension seperti jpg, png, webp, apk, dll
     *
     * @param file yang mau di dapatkan extensionnya
     * @return
     */
    public String getFileExtension(File file) {
        if (null != file) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }

        return "";
    }

    /**
     * Untuk Dapetin file mime type seperti image/jpg, dll
     *
     * @param file yang mau di dapatkan mimeTypenya
     * @return
     */
    public String getFileMimeType(File file) {
        return URLConnection.guessContentTypeFromName(file.getName());
    }
}
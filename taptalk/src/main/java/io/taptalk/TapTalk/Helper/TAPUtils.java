package io.taptalk.TapTalk.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity;
import io.taptalk.TapTalk.Helper.CustomTabLayout.TAPCustomTabActivityHelper;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPMapActivity;
import io.taptalk.TapTalk.View.Activity.TAPWebBrowserActivity;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static androidx.core.util.PatternsCompat.WEB_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.INTENT_TYPE_ALL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.INTENT_TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.INTENT_TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.OPEN_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IntentType.SELECT_PICTURE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.AUDIO_MP3;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.VIDEO_MP4;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_POST_NOTIFICATIONS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_PROFILE_PICTURE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.DESCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;

public class TAPUtils {

    private static final String TAG = TAPUtils.class.getSimpleName();

    private static ObjectMapper objectMapper;

    public static ObjectMapper createObjectMapper() {
        if (null == objectMapper) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        }
        return objectMapper;
    }

    public static String toJsonString(Object object) {
        try {
            return createObjectMapper().writeValueAsString(object);
        }
        catch (Exception e) {
            Log.e(TAG, "toJsonString exception: ", e);
            return "{}";
        }
    }

    public static JSONObject toJsonObject(Object object) {
        try {
            return new JSONObject(createObjectMapper().writeValueAsString(object));
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        try {
            return createObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {
            Log.e(TAG, "fromJSON: ", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> toHashMap(Object object) {
        try {
            return createObjectMapper().convertValue(object, HashMap.class);
        } catch (Exception e) {
            Log.e(TAG, "toHashMap: ", e);
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, Object> toHashMap(String jsonString) {
        try {
            return createObjectMapper().readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertObject(Object fromObject, TypeReference<T> toObjectType) {
        try {
            return createObjectMapper().convertValue(fromObject, toObjectType);
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
    public static String generateRandomString(int length) {
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
    public static int generateRandomNumber(int randomNum) {
        Random rnd = new Random();
        return rnd.nextInt(randomNum) + 1;
    }

    /**
     * substring for chat above char limit
     */
    public static String mySubString(String myString, int start, int length) {
        return myString.substring(start, Math.min(start + length, myString.length()));
    }

    /**
     * converts Dp into Px
     */
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Resources res, float dp) {
        return (int) (dp * res.getDisplayMetrics().density);
    }

    /**
     * Converts sp to px
     *
     * @param res Resources
     * @param sp  the value in sp
     * @return int
     */
    public static int spToPx(Resources res, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
    }

    public static boolean isRtl(Resources res) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && res.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED &&
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    (!permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    !permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)))
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean allPermissionsGranted(int[] grantResults) {
        if (0 == grantResults.length) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getRandomColor(Context context, String s) {
        if (null == s || s.length() == 0) {
            return 0;
        }
        int[] randomColors = context.getResources().getIntArray(R.array.tapDefaultRoomAvatarBackgroundColors);
        int index = (((int) s.charAt(0)) + (int) s.charAt(s.length() - 1) + s.length()) % randomColors.length;
        return randomColors[index];
    }

    public static String getInitials(String s, int maxLength) {
        if (null == s || s.length() == 0) {
            return "";
        }
        String initials = s.replaceAll("([^\\s])[^\\s]+", "$1").replaceAll("\\s", "");
        if (initials.length() > maxLength) {
            return initials.substring(0, maxLength);
        }
        return initials;
    }

    public static void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    public static void dismissKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    public static void showKeyboard(Activity activity, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static List<TapContactListModel> generateContactListForRecycler(List<TAPUserModel> contacts, int type) {
        return generateContactListForRecycler(contacts, type, null, null);
    }

    public static List<TapContactListModel> generateContactListForRecycler(List<TAPUserModel> contacts, int type, @Nullable Map<String, TapContactListModel> contactListPointer, List<String> selectedContactsIds) {
        List<TapContactListModel> separatedContacts = new ArrayList<>();
        List<TapContactListModel> nonAlphabeticContacts = new ArrayList<>();
        List<TapContactListModel> filteredContacts = new ArrayList<>();
        for (TAPUserModel contact : contacts) {
            // Check name is not null
            if (null != contact.getFullname() && !contact.getFullname().isEmpty()) {
                TapContactListModel filteredContact = new TapContactListModel(contact, type);
                filteredContact.setSelected(!isListEmpty(selectedContactsIds) && selectedContactsIds.contains(contact.getUserID()));
                filteredContacts.add(filteredContact);
            }
        }
        int previousInitialIndexStart = 0;
        int size = filteredContacts.size();
        for (int i = 1; i <= size; i++) {
            if (i == size ||
                    filteredContacts.get(i).getUser().getFullname().toLowerCase().charAt(0) !=
                            filteredContacts.get(i - 1).getUser().getFullname().toLowerCase().charAt(0)) {
                List<TapContactListModel> contactSubList = filteredContacts.subList(previousInitialIndexStart, i);
                char initial = contactSubList.get(0).getUser().getFullname().toLowerCase().charAt(0);
                //if (Character.isAlphabetic(contactSubList.get(0).getUser().getName().toLowerCase().charAt(0))) {
                if ((initial >= 'a' && initial <= 'z') || (initial >= 'A' && initial <= 'Z')) { // Character.isAlphabetic not available below API 19
                    separatedContacts.add(new TapContactListModel(filteredContacts.get(i - 1).getUser().getFullname().substring(0, 1)));
                    separatedContacts.addAll(contactSubList);
                } else {
                    nonAlphabeticContacts.addAll(contactSubList);
                }
                previousInitialIndexStart = i;
            }
            if (null != contactListPointer) {
                contactListPointer.put(filteredContacts.get(i - 1).getUser().getUserID(), filteredContacts.get(i - 1));
            }
        }
        if (!nonAlphabeticContacts.isEmpty()) {
            separatedContacts.add(new TapContactListModel("#"));
            separatedContacts.addAll(nonAlphabeticContacts);
        }
        return separatedContacts;
    }

    public static String getStringFromURL(URL url) throws IOException {
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

    public static String formatCurrencyRp(long value) {
        String str = String.format(Locale.getDefault(), "%,d", value);
        return "Rp " + str.replace(",", ".");
    }

    public static String formatThousandSeperator(String value) {
        try {
            Long v = Long.valueOf(value);
            String str = String.format(Locale.getDefault(), "%,d", v);
            return str.replace(",", ".");
        } catch (Exception e) {
            return "0";
        }
    }

    public enum ClipType {TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

    public static void clipToRoundedRectangle(View view, int cornerRadius, ClipType clipType) {
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

    @Deprecated
    public static void openProfileActivity(Context context, TAPRoomModel room) {
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

    public static void openLocationPicker(Activity activity, String instanceKey) {
        if (!hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            TAPMapActivity.Companion.start(activity, instanceKey);
        }
    }

    public static String[] getStoragePermissions(boolean includeAudio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (includeAudio) {
                return new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                };
            }
            else {
                return new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                };
            }
        }
        else {
            return new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    public static void pickImageFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (!hasPermissions(activity, getStoragePermissions(false))) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(false), PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {INTENT_TYPE_IMAGE};
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Filter only images
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.setType(INTENT_TYPE_IMAGE);
            try {
                activity.startActivityForResult(Intent.createChooser(intent, SELECT_PICTURE), requestCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    public static void pickMediaFromGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (!hasPermissions(activity, getStoragePermissions(false))) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(false), PERMISSION_READ_EXTERNAL_STORAGE_GALLERY);
        } else {
            // Permission granted
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {INTENT_TYPE_IMAGE, INTENT_TYPE_VIDEO};
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Filter only images and videos
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(INTENT_TYPE_ALL);
            try {
                activity.startActivityForResult(Intent.createChooser(intent, GALLERY), requestCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reminder: Handle onRequestPermissionsResult in activity
     */
    public static void requestPermissionAndOpenMediaPicker(Activity activity, int requestCode, String[] mimeTypes, boolean allowMultiple) {
        if (!hasPermissions(activity, getStoragePermissions(true))) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(true), requestCode);
        } else {
            // Permission granted
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Filter mime types
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(INTENT_TYPE_ALL);
            try {
                activity.startActivityForResult(Intent.createChooser(intent, GALLERY), requestCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return Uri to receive saved image path
     * Reminder: Handle onRequestPermissionsResult in activity using the returned Uri
     */
    public static Uri takePicture(String instanceKey, Activity activity, int requestCode) {
        if (!hasPermissions(activity, Manifest.permission.CAMERA)) {
            // Check camera permission
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !hasPermissions(activity, getStoragePermissions(false))) {
            // Check read & write storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(false), PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA);
        } else {
            // All permissions granted
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                String filename = TAPTimeFormatter.formatTime(System.currentTimeMillis(), "yyyyMMdd_HHmmssSSS");
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(filename, ".jpeg", dir);
                Uri imageUri = FileProvider.getUriForFile(activity, FILEPROVIDER_AUTHORITY, image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                }
                activity.startActivityForResult(intent, requestCode);
                // Save file path to map
                TAPFileDownloadManager.getInstance(instanceKey).addFileProviderPath(imageUri, image.getAbsolutePath());
                return imageUri;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean openFile(String instanceKey, Context context, Uri uri, String mimeType) {
        String path = TAPFileDownloadManager.getInstance(instanceKey).getFileProviderPath(uri);
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
            context.startActivity(Intent.createChooser(intent, OPEN_FILE));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.tap_error_no_app_to_open_file), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static boolean checkAndRequestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!TAPUtils.hasPermissions(activity, android.Manifest.permission.POST_NOTIFICATIONS)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_POST_NOTIFICATIONS);
                return true;
            }
        }
        return false;
    }

    /**
     * Current and maxDuration are in milliseconds
     */
    public static String getMediaDurationString(int current, int maxDuration) {
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

    public static String getMediaDurationStringDummy(int maxDuration) {
        int hourMs = 1000 * 60 * 60;

        if (maxDuration > hourMs) {
            return "00-00-0";
        } else {
            return "00-00";
        }
    }

    public static String getFileDisplayName(TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return getDefaultFileName();
        }

        String fileName = (String) data.get(FILE_NAME);
        if (null != fileName && fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        else if (null != fileName && !fileName.isEmpty()) {
            return fileName;
        }

        String url = (String) data.get(FILE_URL);
        if (url != null && !url.isEmpty()) {
            String nameFromUrl = TAPFileUtils.getFileNameFromURL(url);
            if (nameFromUrl != null) {
                return nameFromUrl;
            }
        }

        return getDefaultFileName();
    }

    private static String getDefaultFileName() {
        if (TapTalk.appContext != null) {
            return TapTalk.appContext.getString(R.string.tap_document);
        }
        return "Document";
    }

    public static String getFileDisplayInfo(TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        String fileName = (String) data.get(FILE_NAME);
        String mediaType = getMimeTypeFromMessage(message);
        Number size = (Number) data.get(SIZE);

        String displaySize = "", displayExtension = "";
        if (null != size && size.longValue() > 0L) {
            displaySize = getStringSizeLengthFile(size.longValue());
        }

        if (null != fileName && fileName.contains(".")) {
            displayExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        else if (null != mediaType && mediaType.contains("/")) {
            displayExtension = mediaType.substring(mediaType.lastIndexOf('/') + 1);
        }
        else if (null != mediaType) {
            displayExtension = mediaType;
        }

        if (!displaySize.isEmpty() && !displayExtension.isEmpty()) {
            return String.format("%s %s", displaySize, displayExtension).toUpperCase();
        }
        else if (!displayExtension.isEmpty()) {
            return displayExtension.toUpperCase();
        }
        else if (!displaySize.isEmpty()) {
            return displaySize;
        }

        return "";
    }

    public static String getFileDisplaySizeAndDate(Context context, TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        Number size = (Number) data.get(SIZE);

        String displaySize = "", displayDate = "", displayTime = "";
        if (null != size && size.longValue() > 0L) {
            displaySize = getStringSizeLengthFile(size.longValue());
        }
        if (null != message.getCreated()) {
            displayDate = TAPTimeFormatter.dateStampString(context, message.getCreated());
            displayTime = TAPTimeFormatter.formatClock(message.getCreated());
        }


        if (!displaySize.isEmpty() && !displayDate.isEmpty() && !displayTime.isEmpty()) {
            return String.format("%s • %s • %s", displaySize.toUpperCase(), displayDate, displayTime);
        } else if (!displayDate.isEmpty()) {
            return String.format("%s • %s", displayDate, displayTime);
        } else if (!displaySize.isEmpty()) {
            return displaySize;
        }
        return "";
    }

    public static String getFileDisplayProgress(TAPMessageModel message, Long progressBytes) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        Number size = (Number) data.get(SIZE);
        if (null != size && size.longValue() > 0L) {
            return String.format("%s / %s", getStringSizeLengthFile(progressBytes), getStringSizeLengthFile(size.longValue()));
        } else {
            return getStringSizeLengthFile(progressBytes);
        }
    }

    public static String getFileDisplayDummyInfo(Context context, TAPMessageModel message) {
        HashMap<String, Object> data = message.getData();
        if (null == data) {
            return "";
        }
        String fileDisplayInfo = getFileDisplayInfo(message);
        Number size = (Number) data.get(SIZE);

        if (null != size) {
            String dummyProgress = String.format(context.getString(R.string.tap_format_s_file_info_progress_dummy), getStringSizeLengthFile(size.longValue()));
            if (dummyProgress.length() > fileDisplayInfo.length()) {
                return dummyProgress;
            }
        }
        return fileDisplayInfo;
    }

    @Nullable
    public static TAPMediaPreviewModel getPreviewFromUri(Context context, Uri uri, boolean isSelected) {
        int messageType = getMessageTypeFromFileUri(context, uri);
        if (messageType == TYPE_IMAGE) {
            Bitmap bitmap = getBitmapFromUri(context, uri);
            if (bitmap != null) {
                File tempFile = TAPFileUtils.createTemporaryCachedBitmap(context, bitmap, IMAGE_JPEG, 100);
                if (tempFile != null) {
                    Uri bitmapUri = Uri.fromFile(tempFile);
                    return TAPMediaPreviewModel.Builder(bitmapUri, messageType, isSelected);
                }
            }
            return null;
        }
        else if (messageType == TYPE_VIDEO) {
            File tempFile = TAPFileUtils.createTemporaryCachedFile(context, uri);
            if (tempFile != null) {
                Uri fileUri = Uri.fromFile(tempFile);
                return TAPMediaPreviewModel.Builder(fileUri, messageType, isSelected);
            }
        }
        return TAPMediaPreviewModel.Builder(uri, messageType, isSelected);
    }

    public static ArrayList<TAPMediaPreviewModel> getPreviewsFromClipData(Context context, ClipData clipData, boolean isFirstSelected) {
        ArrayList<TAPMediaPreviewModel> mediaPreviews = new ArrayList<>();
        int itemSize = clipData.getItemCount();
        for (int count = 0; count < itemSize; count++) {
            Uri uri = clipData.getItemAt(count).getUri();
            boolean isSelected = count == 0 && isFirstSelected;
            TAPMediaPreviewModel preview = getPreviewFromUri(context, uri, isSelected);
            if (preview != null) {
                mediaPreviews.add(preview);
            }
        }
        return mediaPreviews;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            return MediaStore.Images.Media.getBitmap(contentResolver, uri);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getMessageTypeFromFileUri(Context context, Uri uri) {
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

    public static void getUserFromXcUserID(String instanceKey, String xcUserID, TAPDatabaseListener<TAPUserModel> listener) {
        // Get user from Contact Manager
        TAPDataManager.getInstance(instanceKey).getUserWithXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel entity) {
                if (null != entity) {
                    TAPContactManager.getInstance(instanceKey).updateUserData(entity);
                    listener.onSelectFinished(entity);
                } else {
                    // Get user data from API
                    if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
                        TAPDataManager.getInstance(instanceKey).getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetUserResponse response) {
                                TAPUserModel userResponse = response.getUser();
                                TAPContactManager.getInstance(instanceKey).updateUserData(userResponse);
                                listener.onSelectFinished(userResponse);
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.onSelectFailed(error.getMessage());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
                                    TAPDataManager.getInstance(instanceKey).getUserByXcUserIdFromApi(xcUserID, this);
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

    public static int adjustAlpha(@ColorInt int color, float factor) {
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
    public static void convertActivityFromTranslucent(Activity activity) {
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
    public static void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(Activity activity) {
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

    public static void showNoInternetErrorDialog(Context context) {
        new TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(context.getString(R.string.tap_error))
                .setMessage(context.getString(R.string.tap_no_internet_show_error))
                .setPrimaryButtonTitle(context.getString(R.string.tap_ok))
                .setPrimaryButtonListener(v -> {
                }).show();
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    /**
     * Convert Bitmap to File for Retrofit
     */
    public static File createTempFile(Context context, String mimeType, Bitmap bitmap) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

    public static int searchMessagePositionByLocalID(List<TAPMessageModel> messageModels, String localID) {
        for (int index = 0; index < messageModels.size(); index++) {
            if (localID.equals(messageModels.get(index).getLocalID())) {
                return index;
            }
        }
        return -1;
    }

    public static void openCustomTabLayout(Activity activity, String url) {
        if (activity == null || url == null || url.isEmpty()) {
            return;
        }
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(activity, R.color.tapColorPrimary));
        intentBuilder.setShowTitle(true);
        intentBuilder.setStartAnimations(activity, R.anim.tap_slide_up, R.anim.tap_stay);
        intentBuilder.setExitAnimations(activity, R.anim.tap_stay,
                R.anim.tap_slide_down);

        TAPCustomTabActivityHelper.openCustomTab(activity,
                intentBuilder.build(), Uri.parse(url), (activity1, uri) -> {
                    Intent intent = new Intent(activity1, TAPWebBrowserActivity.class);
                    intent.putExtra(TAPWebBrowserActivity.EXTRA_URL, uri.toString());
                    activity1.startActivity(intent);
                });
    }

    public static void openUrl(Activity activity, String url) {
        if (activity == null || url == null || url.isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    public static void openUrl(String instanceKey, Activity activity, String url) {
        if (instanceKey == null || activity == null || url == null || url.isEmpty()) {
            return;
        }
        if (!TapUI.getInstance(instanceKey).isOpenLinkWithExternalBrowserEnabled()) {
            openCustomTabLayout(activity, url);
        } else {
            openUrl(activity, url);
        }
    }

    public static void composeEmail(Activity activity, String emailRecipient) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(emailRecipient));
        activity.startActivity(Intent.createChooser(intent, "Send mail"));
    }

    public static void openDialNumber(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if (!phoneNumber.startsWith("tel:")) {
            phoneNumber = "tel:" + phoneNumber;
        }
        intent.setData(Uri.parse(phoneNumber));
        activity.startActivity(intent);
    }

    @SuppressLint("IntentReset")
    public static boolean composeSMS(Activity activity, String phoneNumber) {
        try {
            Uri smsUri = Uri.parse("tel:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("address", phoneNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            return true;
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getDefaultSmsAppPackageName(Context context) {
        String defaultSmsPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            return defaultSmsPackageName;
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty()) {
                return resolveInfos.get(0).activityInfo.packageName;
            }
        }
        return null;
    }

    public static void openMaps(Activity activity, Double latitude, Double longitude) {
        Uri googleMapUrl = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude + "&z=16");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapUrl);
        activity.startActivity(mapIntent);
    }

    /**
     * Custom File Picker
     */
    @Deprecated
    public static void openDocumentPicker(Activity activity) {
        if (!hasPermissions(activity, getStoragePermissions(true))) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(true), PERMISSION_READ_EXTERNAL_STORAGE_FILE);
        } else {
            // Permission granted
            Intent intent = new Intent(activity, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.ARG_HIDDEN, true);
            intent.setType(INTENT_TYPE_ALL);
            activity.startActivityForResult(intent, SEND_FILE);
            activity.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    /**
     * Default File Picker
     */
    public static void openDocumentPicker(Activity activity, int requestCode) {
        if (!hasPermissions(activity, getStoragePermissions(true))) {
            // Check read storage permission
            ActivityCompat.requestPermissions(activity, getStoragePermissions(true), PERMISSION_READ_EXTERNAL_STORAGE_FILE);
        } else {
            // Permission granted
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {INTENT_TYPE_ALL};
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(INTENT_TYPE_ALL);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple); // Allow multiple select
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.setType(INTENT_TYPE_ALL);
            try {
                activity.startActivityForResult(Intent.createChooser(intent, GALLERY), requestCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isListEmpty(List t) {
        return null == t || 0 >= t.size();
    }

    public static String getStringSizeLengthFile(long size) {

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

    public static String getFileExtension(File file) {
        if (null != file) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public static String getFileMimeType(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        fileName = fileName.replaceAll("[^a-zA-Z0-9 .]", "");
        try {
            return URLConnection.guessContentTypeFromName(fileName);
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String getImageMimeType(Context context, Uri imageUri) {
        if (null != imageUri && null != imageUri.getScheme() && imageUri.getScheme().contains("content")) {
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType != null && !mimeType.isEmpty()) {
                return mimeType;
            }
        }
        else if (null != imageUri) {
            String mimeType = getFileMimeType(new File(imageUri.toString()));
            if (mimeType != null && !mimeType.isEmpty()) {
                return mimeType;
            }
        }
        return IMAGE_JPEG;
    }

    public static String getMimeTypeFromUrl(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return "";
            }
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

    public static void rotateAnimateInfinitely(Context context, View view) {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.tap_rotation_infinite);
        rotation.setFillAfter(true);
        view.startAnimation(rotation);
    }

    public static void stopViewAnimation(View view) {
        view.clearAnimation();
    }


    public static void animateClickButton(View view, Float resize) {
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

    public static String getDeviceCountryCode(Context context) {
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
    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = 0;
            if (homeOperator != null && homeOperator.length() > 3) {
                mcc = Integer.parseInt(homeOperator.substring(0, 3));
            }

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
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean listEqualsIgnoreOrder(List<TAPUserModel> list1, List<TAPUserModel> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public static String getFirstWordOfString(String text) {
        if (text.contains(" ")) {
            return text.substring(0, text.indexOf(' '));
        } else {
            return text;
        }
    }

    public static String concatStringList(ArrayList<String> words) {
        StringBuilder resultString = new StringBuilder();
        if (words.size() > 1) {
            int index = 0;
            for (String word : words) {
                resultString.append(word);
                index++;
                if (index != words.size()) {
                    resultString.append(", ");
                }
            }
        } else {
            resultString.append(words.get(0));
        }

        return resultString.toString();
    }

    public static String removeNonAlphaNumeric(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "");
    }

    public static String getLastCharacters(String s, int length) {
        if (s.length() <= length) {
            return s;
        }
        return s.substring(s.length() - length);
    }

    public static String getUriKeyFromUrl(String url) {
        return removeNonAlphaNumeric(url).toLowerCase();
    }

    /**
     * @return key String to get file message Uri from TapDownloadManager
     */
    public static String getUriKeyFromMessage(TAPMessageModel message) {
        if (null == message.getData()) {
            return "";
        }
        String fileUrl = (String) message.getData().get(FILE_URL);
        if (null != fileUrl && !fileUrl.isEmpty()) {
            return getUriKeyFromUrl(fileUrl);
        }
        String fileID = (String) message.getData().get(FILE_ID);
        if (null != fileID && !fileID.isEmpty()) {
            return fileID;
        }
        return "";
    }

    public static boolean isActiveUserMentioned(TAPMessageModel message, TAPUserModel activeUser) {
        if (message.getUser().equals(activeUser) ||
                message.getRoom().getType() == TYPE_PERSONAL ||
                (message.getType() != TYPE_TEXT &&
                        message.getType() != TYPE_LINK &&
                        message.getType() != TYPE_IMAGE &&
                        message.getType() != TYPE_VIDEO) ||
                null == activeUser.getUsername() ||
                activeUser.getUsername().isEmpty()) {
            return false;
        }
        String text = message.getBody();
        if (null == text || text.isEmpty()) {
            return false;
        }
        return text.contains(" @" + activeUser.getUsername() + " ") ||
                text.contains(" @" + activeUser.getUsername() + "\n") ||
                (text.contains(" @" + activeUser.getUsername()) && text.endsWith(activeUser.getUsername())) ||
                text.contains("\n@" + activeUser.getUsername() + " ") ||
                text.contains("\n@" + activeUser.getUsername() + "\n") ||
                (text.contains("\n@" + activeUser.getUsername()) && text.endsWith(activeUser.getUsername())) ||
                text.startsWith("@" + activeUser.getUsername()) && text.contains("@" + activeUser.getUsername() + " ") ||
                text.startsWith("@" + activeUser.getUsername()) && text.contains("@" + activeUser.getUsername() + "\n") ||
                text.equals("@" + activeUser.getUsername());
    }

    public static void handleReceivedSystemMessage(String instanceKey, TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
        String activeUserID = activeUser.getUserID();
        if (message.getAction() != null && message.getAction().equals(DELETE_USER)) {
            // Save user data
            TAPContactManager.getInstance(instanceKey).updateUserData(message.getUser());

            // Set value for message.user.deleted for existing messages from the deleted user
            TAPDataManager.getInstance(instanceKey).getMessageBySenderUserIDFromDatabase(message.getUser().getUserID(), new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onSelectFinished(List<TAPMessageEntity> entities) {
                    List<TAPMessageEntity> updatedEntities = new ArrayList<>();
                    for (TAPMessageEntity deletedUserMessage : entities) {
                        deletedUserMessage.setUserDeleted(message.getUser().getDeleted());
                        updatedEntities.add(deletedUserMessage);
                    }
                    TAPDataManager.getInstance(instanceKey).insertToDatabase(updatedEntities, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            // Set value for message.room.deleted for existing messages in deleted user's room
                            String roomID = TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                                    TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                                    message.getUser().getUserID());
                            TAPDataManager.getInstance(instanceKey).getAllMessagesInRoomFromDatabase(roomID, false, new TAPDatabaseListener<TAPMessageEntity>() {
                                @Override
                                public void onSelectFinished(List<TAPMessageEntity> entities) {
                                    List<TAPMessageEntity> updatedEntities = new ArrayList<>();
                                    for (TAPMessageEntity deletedRoomMessage : entities) {
                                        deletedRoomMessage.setRoomLocked(true);
                                        deletedRoomMessage.setRoomDeleted(true);
                                        deletedRoomMessage.setRoomDeletedTimestamp(message.getUser().getDeleted());
                                        updatedEntities.add(deletedRoomMessage);
                                    }
                                    TAPDataManager.getInstance(instanceKey).insertToDatabase(updatedEntities, false);
                                }
                            });
                        }
                    });
                }
            });
        }
        else if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                null != message.getAction() &&
                message.getAction().equals(UPDATE_USER) &&
                null != message.getUser() &&
                null != message.getUser().getUpdated() &&
                null != activeUser.getUpdated() &&
                message.getUser().getUpdated() > activeUser.getUpdated() &&
                message.getUser().getUserID().equals(activeUserID)
        ) {
            // Update user avatar
            activeUser.updateValue(message.getUser());
            TAPDataManager.getInstance(instanceKey).saveActiveUser(activeUser);
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(new Intent(RELOAD_PROFILE_PICTURE));
        }
    }

    public static void mergeSort(List<TAPMessageModel> messages, int sortDirection) {
        int messageListSize = messages.size();

        if (messageListSize < 2) {
            return;
        }

        int leftListSize = messageListSize / 2;
        int rightListSize = messageListSize - leftListSize;
        List<TAPMessageModel> leftList = new ArrayList<>(leftListSize);
        List<TAPMessageModel> rightList = new ArrayList<>(rightListSize);

        for (int index = 0; index < leftListSize; index++)
            leftList.add(index, messages.get(index));

        for (int index = leftListSize; index < messageListSize; index++)
            rightList.add((index - leftListSize), messages.get(index));

        mergeSort(leftList, sortDirection);
        mergeSort(rightList, sortDirection);

        merge(messages, leftList, rightList, leftListSize, rightListSize, sortDirection);
    }

    private static void merge(List<TAPMessageModel> messagesAll, List<TAPMessageModel> leftList, List<TAPMessageModel> rightList, int leftSize, int rightSize, int sortDirection) {
        int indexLeft = 0, indexRight = 0, indexCombine = 0;

        while (indexLeft < leftSize && indexRight < rightSize) {
            if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() < rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() >= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() > rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() <= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            }
        }

        while (indexLeft < leftSize) {
            messagesAll.set(indexCombine, leftList.get(indexLeft));
            indexLeft += 1;
            indexCombine += 1;
        }

        while (indexRight < rightSize) {
            messagesAll.set(indexCombine, rightList.get(indexRight));
            indexRight += 1;
            indexCombine += 1;
        }
    }

    public static boolean isSavedMessagesRoom(String roomID, String instanceKey) {
        if (roomID == null || instanceKey == null) {
            return false;
        }
        TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
        if (activeUser == null) {
            return false;
        }
        String userID = activeUser.getUserID();
        if (userID == null || userID.isEmpty()) {
            return false;
        }
        return roomID.equals(String.format("%s-%s", userID, userID));
    }

    public static List<String> getUrlsFromString(String text) {
        String [] parts = text.split("\\s+");
        List<String> urls = new ArrayList<>();
        // get every part
        for( String item : parts ) {
            if(WEB_URL.matcher(item).matches()) {
                urls.add(item);
            }
        }
        return urls;
    }

    public static String getFirstUrlFromString(String text) {
        String [] parts = text.split("\\s+");
        for (String item : parts) {
            if (WEB_URL.matcher(item).matches()) {
                return item;
            }
        }
        return "";
    }

    public static String setUrlWithProtocol(String url) {
        if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public static String beautifyPhoneNumber(String phoneNumber, boolean insertPlus) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }
        try {
            String plus = insertPlus ? "+" : "";
            boolean hasIDCountryCodePrefix;
            String phoneWithoutCode;

            // Obtain trimmed phone without code
            if (phoneNumber.startsWith("+62")) {
                phoneWithoutCode = phoneNumber.substring(3).trim();
                hasIDCountryCodePrefix = true;
            }
            else if (phoneNumber.startsWith("62")) {
                phoneWithoutCode = phoneNumber.substring(2).trim();
                hasIDCountryCodePrefix = true;
            }
            else {
                phoneWithoutCode = phoneNumber.trim();
                hasIDCountryCodePrefix = false;
            }

            try {
                Pattern notNumberPattern = Pattern.compile("[^0-9]");
                Matcher notNumberMatcher = notNumberPattern.matcher(phoneWithoutCode);
                if (notNumberMatcher.find()) {
                    // Return original string if not number
                    return phoneNumber;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (phoneWithoutCode.length() < 6) {
                // Skip beautification if length < 6
                if (hasIDCountryCodePrefix) {
                    return String.format("%s62 %s", plus, phoneWithoutCode);
                }
                return String.format("%s%s", plus, phoneNumber);
            }

            // Calculate three digit segment amount (0 to 3)
            int threeDigitSegments = phoneWithoutCode.length() % 4;
            if (threeDigitSegments <= 0) {
                threeDigitSegments = 0;
            }
            else {
                threeDigitSegments = 4 - threeDigitSegments;
            }

            // Insert spaces
            StringBuilder beautifiedPhoneNumber = new StringBuilder(phoneWithoutCode);
            int insertIndex = phoneWithoutCode.length();
            while (insertIndex > 0) {
                if (threeDigitSegments > 0) {
                    insertIndex -= 3;
                }
                else {
                    insertIndex -= 4;
                }
                if (insertIndex > 0) {
                    beautifiedPhoneNumber.insert(insertIndex, " ");
                }
                threeDigitSegments--;
            }

            // Return beautified number
            if (hasIDCountryCodePrefix) {
                return String.format("%s62 %s", plus, beautifiedPhoneNumber);
            }
            return String.format("%s%s", plus, beautifiedPhoneNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
            return phoneNumber;
        }
    }

    public static String getMimeTypeFromMessage(TAPMessageModel message) {
        if (message.getData() == null) {
            return "";
        }
        String mimeType = (String) message.getData().get(MEDIA_TYPE);
        if (mimeType == null || mimeType.isEmpty()) {
            String url = (String) message.getData().get(FILE_URL);
            mimeType = TAPUtils.getMimeTypeFromUrl(url);
            if (mimeType == null || mimeType.isEmpty()) {
                if (message.getType() == TYPE_IMAGE) {
                    mimeType = IMAGE_JPEG;
                }
                else if (message.getType() == TYPE_VIDEO) {
                    mimeType = VIDEO_MP4;
                }
                else if (message.getType() == TYPE_VOICE) {
                    mimeType = AUDIO_MP3;
                }
                else {
                    mimeType = "application/octet-stream";
                }
            }
        }
        return mimeType;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        }
        else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void removeUnderline(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}

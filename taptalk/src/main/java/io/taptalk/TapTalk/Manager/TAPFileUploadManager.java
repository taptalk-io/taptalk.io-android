package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataFileModel;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_IMAGE_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_UPLOAD_CANCELLED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_URI_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_IMAGE_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_UPLOAD_CANCELLED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_URI_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_PNG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailedErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgress;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPFileUploadManager {

    private final String TAG = TAPFileUploadManager.class.getSimpleName();
    private static HashMap<String, TAPFileUploadManager> instances;

    private String instanceKey = "";
    private HashMap<String, List<TAPMessageModel>> uploadQueuePerRoom;
    private HashMap<String, Bitmap> bitmapQueue; // Used for sending images with bitmap
    private HashMap<String, Integer> uploadProgressMapPercent;
    private HashMap<String, Long> uploadProgressMapBytes;
    private HashMap<String, TapSendMessageInterface> sendMessageListeners;

    public static TAPFileUploadManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPFileUploadManager instance = new TAPFileUploadManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPFileUploadManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPFileUploadManager(String instanceKey) {
        this.instanceKey = instanceKey;
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
    }

    private TapTalkSocketInterface socketListener = new TapTalkSocketInterface() {
        @Override
        public void onSocketConnected() {
            checkAndUploadPendingMessages();
        }

        @Override
        public void onSocketDisconnected() {

        }

        @Override
        public void onSocketConnecting() {

        }

        @Override
        public void onSocketError() {

        }
    };

    public Long getMaxFileUploadSize() {
        String maxFileSize = TapTalk.getCoreConfigs(instanceKey).get(CHAT_MEDIA_MAX_FILE_SIZE);
        return null == maxFileSize ? Long.valueOf(DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE) : Long.valueOf(maxFileSize);
    }

    public Long getMaxRoomPhotoUploadSize() {
        String maxFileSize = TapTalk.getCoreConfigs(instanceKey).get(ROOM_PHOTO_MAX_FILE_SIZE);
        return null == maxFileSize ? Long.valueOf(DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE) : Long.valueOf(maxFileSize);
    }

    public Long getMaxUserPhotoUploadSize() {
        String maxFileSize = TapTalk.getCoreConfigs(instanceKey).get(USER_PHOTO_MAX_FILE_SIZE);
        return null == maxFileSize ? Long.valueOf(DEFAULT_USER_PHOTO_MAX_FILE_SIZE) : Long.valueOf(maxFileSize);
    }

    public HashMap<String, TapSendMessageInterface> getSendMessageListeners() {
        return null == sendMessageListeners ? sendMessageListeners = new LinkedHashMap<>() : sendMessageListeners;
    }

    private HashMap<String, Integer> getUploadProgressMapPercent() {
        return null == uploadProgressMapPercent ? uploadProgressMapPercent = new LinkedHashMap<>() : uploadProgressMapPercent;
    }

    private HashMap<String, Long> getUploadProgressMapBytes() {
        return null == uploadProgressMapBytes ? uploadProgressMapBytes = new LinkedHashMap<>() : uploadProgressMapBytes;
    }

    public Integer getUploadProgressPercent(String localID) {
        if (!getUploadProgressMapPercent().containsKey(localID)) {
            return null;
        } else {
            return getUploadProgressMapPercent().get(localID);
        }
    }

    public Long getUploadProgressBytes(String localID) {
        if (!getUploadProgressMapBytes().containsKey(localID)) {
            return null;
        } else {
            return getUploadProgressMapBytes().get(localID);
        }
    }

    public void addUploadProgressMap(String localID, int percent, long bytes) {
        getUploadProgressMapPercent().put(localID, percent);
        getUploadProgressMapBytes().put(localID, bytes);
    }

    public void removeUploadProgressMap(String localID) {
        getUploadProgressMapPercent().remove(localID);
        getUploadProgressMapBytes().remove(localID);
    }

    private HashMap<String, List<TAPMessageModel>> getUploadQueuePerRoom() {
        return null == uploadQueuePerRoom ? uploadQueuePerRoom = new LinkedHashMap<>() : uploadQueuePerRoom;
    }

    public HashMap<String, Bitmap> getBitmapQueue() {
        return null == bitmapQueue ? bitmapQueue = new LinkedHashMap<>() : bitmapQueue;
    }

    public void addUploadQueue(TAPMessageModel messageModel) {
        String roomID = messageModel.getRoom().getRoomID();

        if (getUploadQueuePerRoom().containsKey(roomID) && null != getUploadQueue(roomID)) {
            getUploadQueue(roomID).add(messageModel);
        } else {
            List<TAPMessageModel> tempMessageModels = new ArrayList<>();
            tempMessageModels.add(messageModel);
            getUploadQueuePerRoom().put(roomID, tempMessageModels);
        }
    }

    private List<TAPMessageModel> getUploadQueue(String roomID) {
        if (!getUploadQueuePerRoom().containsKey(roomID))
            return new ArrayList<>();

        return getUploadQueuePerRoom().get(roomID);
    }

    private int getUploadQueueSize(String roomID) {
        if (!getUploadQueuePerRoom().containsKey(roomID) && null != getUploadQueue(roomID))
            return 0;

        return getUploadQueue(roomID).size();
    }

    private boolean isUploadQueueEmpty(String roomID) {
        if (!getUploadQueuePerRoom().containsKey(roomID) && null != getUploadQueue(roomID))
            return false;

        return getUploadQueue(roomID).isEmpty();
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel) {
        addUploadQueue(messageModel);
        if (1 == getUploadQueueSize(roomID)) {
            if (TapCoreMessageManager.getInstance(instanceKey).isUploadMessageFileToExternalServerEnabled()) {
                requestFileUploadToExternalServer(context, roomID);
            } else if (TYPE_IMAGE == messageModel.getType()) {
                uploadImage(context, roomID);
            } else if (TAPDefaultConstant.MessageType.TYPE_VIDEO == messageModel.getType()) {
                uploadVideo(context, roomID);
            } else if (TAPDefaultConstant.MessageType.TYPE_FILE == messageModel.getType()) {
                uploadFile(context, roomID);
            } else if (TAPDefaultConstant.MessageType.TYPE_AUDIO == messageModel.getType()) {
                uploadAudio(context, roomID);
            }
        }
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel, TapSendMessageInterface listener) {
        if (null != listener) {
            getSendMessageListeners().put(messageModel.getLocalID(), listener);
        }
        addUploadQueue(context, roomID, messageModel);
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel, Bitmap bitmap) {
        getBitmapQueue().put(messageModel.getLocalID(), bitmap);
        addUploadQueue(context, roomID, messageModel);
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel, Bitmap bitmap, TapSendMessageInterface listener) {
        getBitmapQueue().put(messageModel.getLocalID(), bitmap);
        if (null != listener) {
            getSendMessageListeners().put(messageModel.getLocalID(), listener);
        }
        addUploadQueue(context, roomID, messageModel);
    }

    public void uploadRoomPicture(Context context, Uri imageUri, String roomID,
                                  TAPDefaultDataView<TAPUpdateRoomResponse> uploadProfilePictureView) {
        createAndResizeImageFile(context, imageUri, IMAGE_MAX_DIMENSION, bitmap -> {
            String mimeType = TAPUtils.getImageMimeType(context, imageUri);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
            File imageFile = TAPUtils.createTempFile(context, mimeTypeExtension, bitmap);

            TAPDataManager.getInstance(instanceKey).uploadRoomPicture(imageFile, mimeType, roomID, uploadProfilePictureView);
        });
    }

    public void uploadProfilePicture(Context context, Uri imageUri, String userID) {
        createAndResizeImageFile(context, imageUri, IMAGE_MAX_DIMENSION, bitmap -> {
            ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
                @Override
                public void onProgressUpdate(int percentage, long bytes) {
                    addUploadProgressMap(userID, percentage, bytes);
                    Intent intent = new Intent(UploadProgressLoading);
                    intent.putExtra(UploadProgress, percentage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onError() {
                    removeUploadProgressMap(userID);
                }

                @Override
                public void onFinish() {
                    removeUploadProgressMap(userID);
                }
            };

            TAPDefaultDataView<TAPGetUserResponse> uploadProfilePictureView = new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    TAPDataManager.getInstance(instanceKey).saveActiveUser(response.getUser());

                    Intent intent = new Intent(UploadProgressFinish);
                    intent.putExtra(K_USER, response.getUser());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onError(TAPErrorModel error) {
                    Intent intent = new Intent(UploadFailed);
                    intent.putExtra(K_USER_ID, userID);
                    intent.putExtra(UploadFailedErrorMessage, error.getMessage());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onError(String errorMessage) {
                    Intent intent = new Intent(UploadFailed);
                    intent.putExtra(K_USER_ID, userID);
                    intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_upload_profile_picture));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            };

            String mimeType = TAPUtils.getImageMimeType(context, imageUri);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
            File imageFile = TAPUtils.createTempFile(context, mimeTypeExtension, bitmap);
            TAPDataManager.getInstance(instanceKey).uploadProfilePicture(imageFile, mimeType, uploadCallbacks, uploadProfilePictureView);
        });
    }

    private void requestFileUploadToExternalServer(Context context, String roomID) {
        if (isUploadQueueEmpty(roomID)) {
            // Queue is empty
            return;
        }

        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            // No internet connection
            return;
        }

        TAPMessageModel messageModel = getUploadQueue(roomID).get(0);
        HashMap<String, Object> messageData = messageModel.getData();

        if (null == messageData) {
            // No data / Uri
            Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
            getUploadQueue(roomID).remove(0);
            getBitmapQueue().remove(messageModel.getLocalID());
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        String fileUri = (String) messageData.get(FILE_URI);

        if (null == fileUri) {
            // Image data does not contain Uri
            Log.e(TAG, context.getString(R.string.tap_error_message_uri_empty));
            getUploadQueue(roomID).remove(0);
            getBitmapQueue().remove(messageModel.getLocalID());
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        if (messageModel.getType() == TYPE_IMAGE) {
            // Generate small thumbnail for image
            createAndResizeImageFile(context, Uri.parse(fileUri), THUMB_MAX_DIMENSION, thumbBitmap -> {
                String thumbBase64 = TAPFileUtils.encodeToBase64(thumbBitmap);
                messageData.put(THUMBNAIL, thumbBase64);
                messageModel.setData(messageData);
                TAPChatManager.getInstance(instanceKey).triggerRequestMessageFileUpload(messageModel, Uri.parse(fileUri));
            });
        } else if (messageModel.getType() == TYPE_VIDEO) {
            TAPDataImageModel videoData = new TAPDataImageModel(messageModel.getData());
            Uri videoUri = Uri.parse(videoData.getFileUri());
            File videoFile = new File(videoUri.toString());
            String mimeType = TAPUtils.getFileMimeType(videoFile);

            if (videoFile.length() == 0 && null != videoData.getFileUri() && !videoData.getFileUri().isEmpty()) {
                // Get video file from file Uri if map is empty
                videoUri = Uri.parse(videoData.getFileUri());
                videoFile = new File(TAPFileUtils.getFilePath(context, videoUri));
                mimeType = context.getContentResolver().getType(videoUri);
            }
            if (videoFile.length() == 0) {
                // File not found
                Log.e(TAG, context.getString(R.string.tap_error_message_uri_empty));
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
                return;
            }

            // Update message data
            videoData.setMediaType(mimeType);
            videoData.setSize(videoFile.length());
            messageModel.putData(videoData.toHashMap());
            TAPChatManager.getInstance(instanceKey).triggerRequestMessageFileUpload(messageModel, Uri.parse(fileUri));
        } else {
            // TODO: 20/04/22 check for audio type MU
            TAPChatManager.getInstance(instanceKey).triggerRequestMessageFileUpload(messageModel, Uri.parse(fileUri));
        }
    }

    private void uploadImage(Context context, String roomID) {
        if (isUploadQueueEmpty(roomID)) {
            // Queue is empty
            return;
        }

        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            // No internet connection
            return;
        }

        TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

        if (null == messageModel.getData()) {
            // No data
            Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
            getUploadQueue(roomID).remove(0);
            getBitmapQueue().remove(messageModel.getLocalID());
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        TAPDataImageModel imageData = new TAPDataImageModel(messageModel.getData());
        System.gc();

        // Create and resize image file
        if (null != imageData.getFileUri() && !imageData.getFileUri().isEmpty()) {
            // Create image from file Uri
            Uri imageUri = Uri.parse(imageData.getFileUri());
            createAndResizeImageFile(context, imageUri, IMAGE_MAX_DIMENSION, bitmap ->
                    // Create thumbnail
                    createAndResizeImageFile(context, imageUri, THUMB_MAX_DIMENSION, thumbBitmap -> {
                        String thumbBase64 = TAPFileUtils.encodeToBase64(thumbBitmap);
                        checkAndUploadCompressedImage(context, roomID, messageModel, imageUri, imageData, bitmap, thumbBase64);
                    }));
        } else if (null != getBitmapQueue().get(messageModel.getLocalID())) {
            // Create image from bitmap queue
            Bitmap bitmap = getBitmapQueue().get(messageModel.getLocalID());
            createAndResizeImageFile(bitmap, IMAGE_MAX_DIMENSION, bitmap1 ->
                    createAndResizeImageFile(bitmap1, THUMB_MAX_DIMENSION, thumbBitmap -> {
                        String thumbBase64 = TAPFileUtils.encodeToBase64(thumbBitmap);
                        checkAndUploadCompressedImage(context, roomID, messageModel, null, imageData, bitmap1, thumbBase64);
                    }));
        } else {
            // Image data does not contain Uri
            Log.e(TAG, context.getString(R.string.tap_error_message_uri_empty));
            getUploadQueue(roomID).remove(0);
            getBitmapQueue().remove(messageModel.getLocalID());
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
        }
    }

    private void checkAndUploadCompressedImage(Context context, String roomID,
                                               TAPMessageModel messageModel, Uri imageUri,
                                               TAPDataImageModel imageData, Bitmap bitmap,
                                               String thumbBase64) {
        if (null == bitmap) {
            triggerSendMessageError(messageModel, ERROR_CODE_IMAGE_UNAVAILABLE, ERROR_MESSAGE_IMAGE_UNAVAILABLE);
            return;
        }
        String mimeType = TAPUtils.getImageMimeType(context, imageUri);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
        File imageFile = TAPUtils.createTempFile(context, mimeTypeExtension, bitmap);

        // Update message data
        imageData.setHeight(bitmap.getHeight());
        imageData.setWidth(bitmap.getWidth());
        imageData.setSize(imageFile.length());
        imageData.setMediaType(mimeType);
        imageData.setThumbnail(thumbBase64);

        messageModel.putData(imageData.toHashMap());

        // Check if upload is cancelled
        if (isUploadQueueEmpty(roomID)) {
            triggerSendMessageError(messageModel, ERROR_CODE_UPLOAD_CANCELLED, ERROR_MESSAGE_UPLOAD_CANCELLED);
            return;
        } else if (0 < getUploadQueue(roomID).size() &&
                !getUploadQueue(roomID).get(0).getLocalID().equals(messageModel.getLocalID())) {
            uploadNextSequence(context, roomID);
            return;
        }
        callImageUploadAPI(context, roomID, messageModel, imageFile, bitmap, thumbBase64, mimeType, imageData);
    }

    private void uploadVideo(Context context, String roomID) {
        if (isUploadQueueEmpty(roomID)) {
            // Queue is empty
            return;
        }

        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            // No internet connection
            return;
        }

        TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

        if (null == messageModel.getData()) {
            // No data
            Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
            getUploadQueue(roomID).remove(0);
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        TAPDataImageModel videoData = new TAPDataImageModel(messageModel.getData());
        Uri videoUri = Uri.parse(videoData.getFileUri());
        File videoFile = new File(videoUri.toString());
        String mimeType = TAPUtils.getFileMimeType(videoFile);

        if (videoFile.length() == 0 && null != videoData.getFileUri() && !videoData.getFileUri().isEmpty()) {
            // Get video file from file Uri if map is empty
            videoUri = Uri.parse(videoData.getFileUri());
            videoFile = new File(TAPFileUtils.getFilePath(context, videoUri));
            mimeType = context.getContentResolver().getType(videoUri);
        }
        if (videoFile.length() == 0) {
            // File not found
            Log.e(TAG, context.getString(R.string.tap_error_message_uri_empty));
            getUploadQueue(roomID).remove(0);
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        // Update message data
        videoData.setMediaType(mimeType);
        videoData.setSize(videoFile.length());

        messageModel.putData(videoData.toHashMap());

        // Check if upload is cancelled
        if (isUploadQueueEmpty(roomID)) {
            triggerSendMessageError(messageModel, ERROR_CODE_UPLOAD_CANCELLED, ERROR_MESSAGE_UPLOAD_CANCELLED);
            return;
        } else if (0 < getUploadQueue(roomID).size() &&
                !getUploadQueue(roomID).get(0).getLocalID().equals(messageModel.getLocalID())) {
            uploadNextSequence(context, roomID);
            return;
        }
        callVideoUploadAPI(context, roomID, messageModel, videoFile, mimeType, videoData);
    }

    private void uploadFile(Context context, String roomID) {
        new Thread(() -> {
            if (isUploadQueueEmpty(roomID)) {
                // Queue is empty
                return;
            }

            if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
                // No internet connection
                return;
            }

            TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

            if (null == messageModel.getData()) {
                // No data
                Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
                return;
            }

            TAPDataFileModel fileData = new TAPDataFileModel(messageModel.getData());
            Uri fileUri = Uri.parse(fileData.getFileUri());

            if (null == fileUri) {
                // File Uri not found
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_not_found));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
                return;
            }

            String pathName = TAPFileUtils.getFilePath(context, fileUri);

            if (null == pathName) {
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_not_found));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
                return;
            }

            File tempFile = new File(pathName);

            // Check if upload is cancelled
            if (isUploadQueueEmpty(roomID)) {
                triggerSendMessageError(messageModel, ERROR_CODE_UPLOAD_CANCELLED, ERROR_MESSAGE_UPLOAD_CANCELLED);
                return;
            } else if (0 < getUploadQueue(roomID).size() &&
                    !getUploadQueue(roomID).get(0).getLocalID().equals(messageModel.getLocalID())) {
                uploadNextSequence(context, roomID);
                return;
            }
            callFileUploadAPI(context, roomID, messageModel, tempFile, fileData.getMediaType());
        }).start();
    }

    private void uploadAudio(Context context, String roomID) {
        if (isUploadQueueEmpty(roomID)) {
            // Queue is empty
            return;
        }

        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            // No internet connection
            return;
        }

        TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

        if (null == messageModel.getData()) {
            // No data
            Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
            getUploadQueue(roomID).remove(0);
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        TAPDataImageModel audioData = new TAPDataImageModel(messageModel.getData());
        Uri audioUri = Uri.parse(audioData.getFileUri());
        File audioFile = new File(audioUri.toString());
        String mimeType = TAPUtils.getFileMimeType(audioFile);

        if (audioFile.length() == 0 && null != audioData.getFileUri() && !audioData.getFileUri().isEmpty()) {
            // Get video file from file Uri if map is empty
            audioUri = Uri.parse(audioData.getFileUri());
            audioFile = new File(TAPFileUtils.getFilePath(context, audioUri));
            mimeType = context.getContentResolver().getType(audioUri);
        }
        if (audioFile.length() == 0) {
            // File not found
            Log.e(TAG, context.getString(R.string.tap_error_message_uri_empty));
            getUploadQueue(roomID).remove(0);
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            triggerSendMessageError(messageModel, ERROR_CODE_URI_NOT_FOUND, ERROR_MESSAGE_URI_NOT_FOUND);
            return;
        }

        // Update message data
        audioData.setMediaType(mimeType);
        audioData.setSize(audioFile.length());

        messageModel.putData(audioData.toHashMap());

        // Check if upload is cancelled
        if (isUploadQueueEmpty(roomID)) {
            triggerSendMessageError(messageModel, ERROR_CODE_UPLOAD_CANCELLED, ERROR_MESSAGE_UPLOAD_CANCELLED);
            return;
        } else if (0 < getUploadQueue(roomID).size() &&
                !getUploadQueue(roomID).get(0).getLocalID().equals(messageModel.getLocalID())) {
            uploadNextSequence(context, roomID);
            return;
        }
        callAudioUploadAPI(context, roomID, messageModel, audioFile, audioData.getMediaType());
    }

    private void callImageUploadAPI(Context context, String roomID, TAPMessageModel messageModel, File imageFile,
                                    Bitmap bitmap, String encodedThumbnail, String mimeType,
                                    TAPDataImageModel imageData) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage, long bytes) {
                addUploadProgressMap(localID, percentage, bytes);
                Intent intent = new Intent(UploadProgressLoading);
                intent.putExtra(UploadLocalID, localID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, percentage, bytes);
                }
            }

            @Override
            public void onError() {
            }

            @Override
            public void onFinish() {
            }
        };

        TAPDefaultDataView<TAPUploadFileResponse> uploadView = new TAPDefaultDataView<TAPUploadFileResponse>() {
            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                super.onSuccess(response, localID);
                saveImageToCacheAndSendMessage(context, roomID, bitmap, encodedThumbnail, messageModel.copyMessageModel(), response);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    long size = 0L;
                    if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                        size = ((Number) messageModel.getData().get(SIZE)).longValue();
                    }
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, 100, size);
                }
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                Uri imageUri = Uri.parse(imageData.getFileUri());
                if (null != messageModel.getData() && null != imageUri.getScheme() && imageUri.getScheme().contains("content")) {
                    messageModel.getData().put(FILE_URI, TAPFileUtils.getFilePath(context, imageUri));
                }
                boolean hasConnection = TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext);
                messageUploadFailed(context, messageModel, roomID, hasConnection, errorMessage);
            }
        };

        // Upload file
        TAPDataManager.getInstance(instanceKey)
                .uploadImage(localID, imageFile,
                        messageModel.getRoom().getRoomID(), imageData.getCaption(),
                        mimeType, uploadCallbacks, uploadView);
    }

    public void uploadImage(Context context, Uri uri, ProgressRequestBody.UploadCallbacks uploadCallback, TAPDefaultDataView<TAPUploadFileResponse> view) {
        TAPFileUploadManager.getInstance(instanceKey).createAndResizeImageFile(context, uri, IMAGE_MAX_DIMENSION, bitmap -> {
            String mimeType = TAPUtils.getImageMimeType(context, uri);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
            File imageFile = TAPUtils.createTempFile(context, mimeTypeExtension, bitmap);
            TAPDataManager.getInstance(instanceKey).uploadImage(null, imageFile, "", "", mimeType, uploadCallback, view);
        });
    }

    private void callVideoUploadAPI(Context context, String roomID, TAPMessageModel messageModel,
                                    File videoFile, String mimeType, TAPDataImageModel videoData) {
        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage, long bytes) {
                addUploadProgressMap(localID, percentage, bytes);
                Intent intent = new Intent(UploadProgressLoading);
                intent.putExtra(UploadLocalID, localID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, percentage, bytes);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TAPDefaultDataView<TAPUploadFileResponse> uploadView = new TAPDefaultDataView<TAPUploadFileResponse>() {

            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                sendFileMessageAfterUploadSuccess(context, roomID, videoFile.getName(), mimeType, messageModel.copyMessageModel(), response);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    long size = 0L;
                    if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                        size = ((Number) messageModel.getData().get(SIZE)).longValue();
                    }
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, 100, size);
                }
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                boolean hasConnection = TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext);
                messageUploadFailed(context, messageModel, roomID, hasConnection, errorMessage);
            }
        };

        // Upload file
        TAPDataManager.getInstance(instanceKey)
                .uploadVideo(localID, videoFile,
                        messageModel.getRoom().getRoomID(), videoData.getCaption(),
                        mimeType, uploadCallbacks, uploadView);
    }

    private void callFileUploadAPI(Context context, String roomID, TAPMessageModel messageModel, File file, String mimeType) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage, long bytes) {
                addUploadProgressMap(localID, percentage, bytes);
                Intent intent = new Intent(UploadProgressLoading);
                intent.putExtra(UploadLocalID, localID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, percentage, bytes);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TAPDefaultDataView<TAPUploadFileResponse> uploadView = new TAPDefaultDataView<TAPUploadFileResponse>() {
            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                super.onSuccess(response, localID);
                sendFileMessageAfterUploadSuccess(context, roomID, file.getName(), mimeType, messageModel.copyMessageModel(), response);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    long size = 0L;
                    if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                        size = ((Number) messageModel.getData().get(SIZE)).longValue();
                    }
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, 100, size);
                }
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                boolean hasConnection = TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext);
                messageUploadFailed(context, messageModel, roomID, hasConnection, errorMessage);
            }
        };

        // Upload file
        TAPDataManager.getInstance(instanceKey)
                .uploadFile(localID, file,
                        messageModel.getRoom().getRoomID(),
                        mimeType, uploadCallbacks, uploadView);
    }

    private void callAudioUploadAPI(Context context, String roomID, TAPMessageModel messageModel, File audioFile, String mimeType) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage, long bytes) {
                addUploadProgressMap(localID, percentage, bytes);
                Intent intent = new Intent(UploadProgressLoading);
                intent.putExtra(UploadLocalID, localID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, percentage, bytes);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TAPDefaultDataView<TAPUploadFileResponse> uploadView = new TAPDefaultDataView<TAPUploadFileResponse>() {
            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                super.onSuccess(response, localID);
                sendFileMessageAfterUploadSuccess(context, roomID, audioFile.getName(), mimeType, messageModel.copyMessageModel(), response);
                if (null != getSendMessageListeners().get(messageModel.getLocalID())) {
                    long size = 0L;
                    if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                        size = ((Number) messageModel.getData().get(SIZE)).longValue();
                    }
                    getSendMessageListeners().get(messageModel.getLocalID()).onProgress(messageModel, 100, size);
                }
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                boolean hasConnection = TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext);
                messageUploadFailed(context, messageModel, roomID, hasConnection, errorMessage);
            }
        };

        // Upload file
        TAPDataManager.getInstance(instanceKey)
                .uploadAudio(localID, audioFile,
                        messageModel.getRoom().getRoomID(),
                        mimeType, uploadCallbacks, uploadView);
    }


    private void messageUploadFailed(
            Context context,
            TAPMessageModel messageModelWithUri,
            String roomID,
            boolean hasConnection,
            String errorMessage
    ) {
        if (TAPChatManager.getInstance(instanceKey).checkMessageIsUploading(messageModelWithUri.getLocalID())) {
            TAPChatManager.getInstance(instanceKey).removeUploadingMessageFromHashMap(messageModelWithUri.getLocalID());
            getBitmapQueue().remove(messageModelWithUri.getLocalID());
            cancelUpload(context, messageModelWithUri, roomID);
            if (hasConnection) {
                messageModelWithUri.setSending(false);
                messageModelWithUri.setFailedSend(true);
                TAPDataManager.getInstance(instanceKey).insertToDatabase(TAPMessageEntity.fromMessageModel(messageModelWithUri));

                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModelWithUri.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, errorMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                triggerSendMessageError(messageModelWithUri, ERROR_CODE_OTHERS, errorMessage);
            }
            else {
                getUploadQueue(roomID).add(messageModelWithUri);
            }
        }
    }

    private void triggerSendMessageError(TAPMessageModel message, String errorCode, String errorMessage) {
        if (null != getSendMessageListeners().get(message.getLocalID())) {
            getSendMessageListeners().get(message.getLocalID()).onError(message, errorCode, errorMessage);
            getSendMessageListeners().remove(message.getLocalID());
        }
    }

    /**
     * Modify image before uploading
     */
    public void createAndResizeImageFile(Context context, Uri imageUri, int imageMaxSize, BitmapInterface bitmapInterface) {
        final String mimeType = TAPUtils.getImageMimeType(context, imageUri);
        new Thread(() -> {
            Bitmap bitmap;
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inSampleSize = calculateInSampleSize(options, IMAGE_MAX_DIMENSION, IMAGE_MAX_DIMENSION);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            } catch (Exception e) {
                // No content provider
//            BitmapFactory.decodeFile(imageUri.toString(), options);
//            options.inJustDecodeBounds = false;
//            bitmap = BitmapFactory.decodeFile(imageUri.toString(), options);
                bitmap = BitmapFactory.decodeFile(imageUri.toString());
            }
            bitmap = resizeBitmap(bitmap, imageMaxSize);
            bitmap = fixImageOrientation(bitmap, imageUri);
            bitmap = compressBitmap(bitmap, mimeType, TapTalk.getImageCompressionQuality(instanceKey));
            bitmapInterface.onBitmapReady(bitmap);
        }).start();
    }

    private void createAndResizeImageFile(Bitmap bitmap, int imageMaxSize, BitmapInterface bitmapInterface) {
        new Thread(() -> {
            Bitmap bitmapEdit = bitmap;
            bitmapEdit = resizeBitmap(bitmapEdit, imageMaxSize);
            // TODO: 3 May 2019 MIME TYPE FORCED TO JPEG
            bitmapEdit = compressBitmap(bitmapEdit, IMAGE_JPEG, TapTalk.getImageCompressionQuality(instanceKey));
            bitmapInterface.onBitmapReady(bitmapEdit);
        }).start();
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int imageMaxSize) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        if (originalWidth > imageMaxSize || originalHeight > imageMaxSize) {
            float scaleRatio = Math.min(
                    (float) imageMaxSize / originalWidth,
                    (float) imageMaxSize / originalHeight);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    Math.round(scaleRatio * originalWidth),
                    Math.round(scaleRatio * originalHeight),
                    false);
        }
        return bitmap;
    }

    private Bitmap fixImageOrientation(Bitmap bitmap, Uri imageUri) {
        String pathName;
        if (null != imageUri.getScheme() && imageUri.getScheme().contains("content")) {
            pathName = TAPFileUtils.getFilePath(appContext, imageUri);
        } else {
            pathName = imageUri.toString();
        }

        int orientation = TAPFileUtils.getImageOrientation(pathName);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    private Bitmap compressBitmap(Bitmap bitmap, String mimeType, int compressionQuality) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // TODO: 3 May 2019 ADD OTHER IMAGE FORMATS (GIF?)
            bitmap.compress(mimeType.equals(IMAGE_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, compressionQuality, os);
            byte[] byteArray = os.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.e(TAG, "calculateInSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    public interface BitmapInterface {
        void onBitmapReady(Bitmap bitmap);
    }

    /**
     * Check upload queue and restart upload sequence
     */
    public void uploadNextSequence(Context context, String roomID) {
        getUploadQueue(roomID).remove(0);
        uploadNextFromQueue(context, roomID);
    }

    private void checkAndUploadPendingMessages() {
        for (Map.Entry<String, List<TAPMessageModel>> entry : getUploadQueuePerRoom().entrySet()) {
            String roomID = entry.getKey();
            uploadNextFromQueue(appContext, roomID);
        }
    }

    private void uploadNextFromQueue(Context context, String roomID) {
        if (!isUploadQueueEmpty(roomID) || 0 < getUploadQueue(roomID).size()) {
            if (null != getUploadQueue(roomID).get(0) && TapCoreMessageManager.getInstance(instanceKey).isUploadMessageFileToExternalServerEnabled()) {
                requestFileUploadToExternalServer(context, roomID);
            } else if (null != getUploadQueue(roomID).get(0) &&
                    TYPE_IMAGE == getUploadQueue(roomID).get(0).getType()) {
                uploadImage(context, roomID);
            } else if (null != getUploadQueue(roomID).get(0) &&
                    TAPDefaultConstant.MessageType.TYPE_VIDEO == getUploadQueue(roomID).get(0).getType()) {
                uploadVideo(context, roomID);
            } else if (null != getUploadQueue(roomID).get(0) &&
                    TAPDefaultConstant.MessageType.TYPE_FILE == getUploadQueue(roomID).get(0).getType()) {
                uploadFile(context, roomID);
            } else if (null != getUploadQueue(roomID).get(0) &&
                    TAPDefaultConstant.MessageType.TYPE_AUDIO == getUploadQueue(roomID).get(0).getType()) {
                uploadAudio(context, roomID);
            }
        }
    }

    /**
     * @param cancelledMessageModel
     */
    public void cancelUpload(Context context, TAPMessageModel cancelledMessageModel, String roomID) {
        int position = TAPUtils.searchMessagePositionByLocalID(getUploadQueue(roomID), cancelledMessageModel.getLocalID());
        removeUploadProgressMap(cancelledMessageModel.getLocalID());
        getBitmapQueue().remove(cancelledMessageModel.getLocalID());
        if (-1 != position && 0 == position && !isUploadQueueEmpty(roomID)) {
            TAPDataManager.getInstance(instanceKey).unSubscribeToUploadImage(roomID);
            uploadNextSequence(context, roomID);
        } else if (-1 != position && !isUploadQueueEmpty(roomID)) {
            getUploadQueue(roomID).remove(position);
        }
    }

    private void saveImageToCacheAndSendMessage(Context context, String roomID, Bitmap bitmap,
                                                String encodedThumbnail, TAPMessageModel messageModel,
                                                TAPUploadFileResponse response) {
        try {
            new Thread(() -> {
                try {
                    String fileID = response.getId();
                    TAPCacheManager.getInstance(context).addBitmapDrawableToCache(fileID, new BitmapDrawable(context.getResources(), bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            String localID = messageModel.getLocalID();
            //long size = 0L;
            //if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
            //    size = ((Number) messageModel.getData().get(SIZE)).longValue();
            //}
            addUploadProgressMap(localID, 100, response.getSize());
            TAPDataImageModel imageDataModel = TAPDataImageModel.Builder(
                    response.getId(),
                    response.getFileURL(),
                    encodedThumbnail,
                    response.getMediaType(),
                    response.getSize(),
                    response.getWidth(),
                    response.getHeight(),
                    response.getCaption());
            HashMap<String, Object> imageDataMap = imageDataModel.toHashMapWithoutFileUri();
            if (null != messageModel.getData()) {
                messageModel.putData(imageDataMap);
                //messageModel.getData().remove(FILE_URI);
            } else {
                messageModel.setData(imageDataMap);
            }

            new Thread(() -> TAPChatManager.getInstance(instanceKey).sendImageMessageToServer(messageModel)).start();

            //removeUploadProgressMap(localID);
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadImageData, imageDataMap);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            uploadNextSequence(context, roomID);
            getBitmapQueue().remove(messageModel.getLocalID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileMessageAfterUploadSuccess(Context context, String roomID, String fileName,
                                                   String mimetype, TAPMessageModel messageModel,
                                                   TAPUploadFileResponse response) {
        try {
            String localID = messageModel.getLocalID();
            //long size = 0L;
            //if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
            //    size = ((Number) messageModel.getData().get(SIZE)).longValue();
            //}
            addUploadProgressMap(localID, 100, response.getSize());

            TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(roomID, response.getId(), (String) messageModel.getData().get(FILE_URI));
            TAPDataFileModel fileDataModel = TAPDataFileModel.Builder(
                    response.getId(),
                    response.getFileURL(),
                    fileName,
                    mimetype,
                    response.getSize());
            HashMap<String, Object> fileDataMap = fileDataModel.toHashMap();
            if (null != messageModel.getData()) {
                messageModel.putData(fileDataMap);
                messageModel.getData().remove(FILE_URI);
            } else {
                messageModel.setData(fileDataMap);
            }

            new Thread(() -> TAPChatManager.getInstance(instanceKey).sendFileMessageToServer(messageModel)).start();

            //removeUploadProgressMap(localID);
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadFileData, fileDataMap);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            uploadNextSequence(context, roomID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFileUploadFromExternalServerFinished(TAPMessageModel messageModel, String fileUrl) {
        HashMap<String, Object> messageData = messageModel.getData();
        if (null == messageData) {
            messageData = new HashMap<>();
        }
        // Update message data with media type and file URL, remove file Uri
        String fileUri = (String) messageData.get(FILE_URI);
        String mimeType = TAPUtils.getMimeTypeFromUrl(fileUrl);
        messageData.put(MEDIA_TYPE, mimeType);
        messageData.put(FILE_URL, fileUrl);
        messageData.remove(FILE_URI);

        // Update upload progress map
        String localID = messageModel.getLocalID();
        removeUploadProgressMap(localID);
        TAPChatManager.getInstance(instanceKey).removeUploadingMessageFromHashMap(localID);

        // Put data to message model
        if (null != messageModel.getData()) {
            messageModel.putData(messageData);
        } else {
            messageModel.setData(messageData);
        }
        //Log.e(TAG, "onFileUploadFromExternalServerFinished messageModel.getData: " + TAPUtils.toJsonString(messageModel.getData()));

        // TODO: 20/04/22 check for audio type MU
        if (messageModel.getType() == TYPE_IMAGE) {
            //Log.e(TAG, "onFileUploadFromExternalServerFinished: send image");
            // Send image message to server
            new Thread(() -> TAPChatManager.getInstance(instanceKey).sendImageMessageToServer(messageModel)).start();

            // Notify message sent
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadImageData, messageModel.getData());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);

            // Check for next upload in queue
            uploadNextSequence(appContext, messageModel.getRoom().getRoomID());
            getBitmapQueue().remove(messageModel.getLocalID());
        } else if (messageModel.getType() == TYPE_VIDEO || messageModel.getType() == TYPE_FILE) {
            //Log.e(TAG, "onFileUploadFromExternalServerFinished: send video/file\n" + fileUrl + "\n" + fileUri);
            // Save Uri map with file URL as key
            TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(
                    messageModel.getRoom().getRoomID(),
                    TAPUtils.removeNonAlphaNumeric(fileUrl).toLowerCase(), fileUri);

            // Send video/file message to server
            new Thread(() -> TAPChatManager.getInstance(instanceKey).sendFileMessageToServer(messageModel)).start();

            // Notify message sent
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadFileData, messageModel.getData());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);

            // Check for next upload in queue
            uploadNextSequence(appContext, messageModel.getRoom().getRoomID());
        }
    }

    public boolean isSizeAllowedForUpload(long size) {
        return getMaxFileUploadSize() >= size;
    }

    public void resetFileUploadManager() {
        getUploadProgressMapBytes().clear();
        getUploadProgressMapPercent().clear();
        getBitmapQueue().clear();
        getUploadQueuePerRoom().clear();
    }
}

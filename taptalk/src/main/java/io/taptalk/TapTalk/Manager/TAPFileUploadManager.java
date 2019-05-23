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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataFileModel;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_PNG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailedErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgress;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;

public class TAPFileUploadManager {

    private final String TAG = TAPFileUploadManager.class.getSimpleName();
    private static TAPFileUploadManager instance;
    private HashMap<String, List<TAPMessageModel>> uploadQueuePerRoom;
    private HashMap<String, Bitmap> bitmapQueue; // Used for sending images with bitmap
    private HashMap<String, Integer> uploadProgressMapPercent;
    private HashMap<String, Long> uploadProgressMapBytes;

    public long maxUploadSize = 25 * 1024 * 1024; // Max size for uploading file message

    public static TAPFileUploadManager getInstance() {
        return null == instance ? instance = new TAPFileUploadManager() : instance;
    }

    public long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
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

    private HashMap<String, Bitmap> getBitmapQueue() {
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

    /**
     * Masukin Message Model ke dalem Queue Upload
     *
     * @param roomID
     * @param messageModel
     */
    // Previously addQueueUploadImage
    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel) {
        addUploadQueue(messageModel);
        if (1 == getUploadQueueSize(roomID) && TAPDefaultConstant.MessageType.TYPE_IMAGE == messageModel.getType()) {
            uploadImage(context, roomID);
        } else if (1 == getUploadQueueSize(roomID) && TAPDefaultConstant.MessageType.TYPE_VIDEO == messageModel.getType()) {
            uploadVideo(context, roomID);
        } else if (1 == getUploadQueueSize(roomID) && TAPDefaultConstant.MessageType.TYPE_FILE == messageModel.getType()) {
            uploadFile(context, roomID);
        }
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel, Bitmap bitmap) {
        getBitmapQueue().put(messageModel.getLocalID(), bitmap);
        addUploadQueue(context, roomID, messageModel);
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

                @Override public void onError() {
                    removeUploadProgressMap(userID);
                }

                @Override public void onFinish() {
                    removeUploadProgressMap(userID);
                }
            };

            TapDefaultDataView<TAPGetUserResponse> uploadProfilePictureView = new TapDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    TAPDataManager.getInstance().saveActiveUser(response.getUser());

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

            String mimeType = TAPUtils.getInstance().getImageMimeType(context, imageUri);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
            File imageFile = TAPUtils.getInstance().createTempFile(mimeTypeExtension, bitmap);
            TAPDataManager.getInstance().uploadProfilePicture(imageFile, mimeType, uploadCallbacks, uploadProfilePictureView);
        });
    }

    private void uploadImage(Context context, String roomID) {
        if (isUploadQueueEmpty(roomID)) {
            // Queue is empty
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
                        String thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(thumbBitmap);
                        checkAndUploadCompressedImage(context, roomID, messageModel, imageUri, imageData, bitmap, thumbBase64);
                    }));
        } else if (null != getBitmapQueue().get(messageModel.getLocalID())) {
            // Create image from bitmap queue
            Bitmap bitmap = getBitmapQueue().get(messageModel.getLocalID());
            createAndResizeImageFile(bitmap, IMAGE_MAX_DIMENSION, bitmap1 ->
                    createAndResizeImageFile(bitmap1, THUMB_MAX_DIMENSION, thumbBitmap -> {
                        String thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(thumbBitmap);
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
        }
    }

    private void checkAndUploadCompressedImage(Context context, String roomID,
                                               TAPMessageModel messageModel, Uri imageUri,
                                               TAPDataImageModel imageData, Bitmap bitmap,
                                               String thumbBase64) {
        if (null == bitmap) {
            return;
        }
        String mimeType = TAPUtils.getInstance().getImageMimeType(context, imageUri);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
        File imageFile = TAPUtils.getInstance().createTempFile(mimeTypeExtension, bitmap);

        // Update message data
        imageData.setHeight(bitmap.getHeight());
        imageData.setWidth(bitmap.getWidth());
        imageData.setSize(imageFile.length());
        imageData.setMediaType(mimeType);
        imageData.setThumbnail(thumbBase64);

        messageModel.putData(imageData.toHashMap());

        //untuk ngecek skali lagi sebelum manggil api, udah d cancel atau belom
        if (isUploadQueueEmpty(roomID))
            return;
        else if (0 < getUploadQueue(roomID).size() &&
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
        TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

        if (null == messageModel.getData()) {
            // No data
            Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
            getUploadQueue(roomID).remove(0);
            Intent intent = new Intent(UploadFailed);
            intent.putExtra(UploadLocalID, messageModel.getLocalID());
            intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }

        TAPDataImageModel videoData = new TAPDataImageModel(messageModel.getData());
        Uri videoUri = Uri.parse(videoData.getFileUri());
        File videoFile = new File(videoUri.toString());
        String mimeType = TAPUtils.getInstance().getFileMimeType(videoFile);

        if (videoFile.length() == 0 && null != videoData.getFileUri() && !videoData.getFileUri().isEmpty()) {
            // Get video file from file Uri if map is empty
            videoUri = Uri.parse(videoData.getFileUri());
            videoFile = new File(TAPFileUtils.getInstance().getFilePath(context, videoUri));
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
            return;
        }

        // Update message data
        videoData.setMediaType(mimeType);
        videoData.setSize(videoFile.length());

        messageModel.putData(videoData.toHashMap());

        // Check if upload is canceled
        if (isUploadQueueEmpty(roomID)) {
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
            TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

            if (null == messageModel.getData()) {
                // No data
                Log.e(TAG, context.getString(R.string.tap_error_message_data_empty));
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_data_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
                return;
            }

            String pathName = TAPFileUtils.getInstance().getFilePath(context, fileUri);

            if (null == pathName) {
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_message_uri_not_found));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }

            File tempFile = new File(pathName);

            //untuk ngecek skali lagi sebelum manggil api, udah d cancel atau belom
            if (isUploadQueueEmpty(roomID))
                return;
            else if (0 < getUploadQueue(roomID).size() &&
                    !getUploadQueue(roomID).get(0).getLocalID().equals(messageModel.getLocalID())) {
                uploadNextSequence(context, roomID);
                return;
            }
            callFileUploadAPI(context, roomID, messageModel, tempFile, fileData.getMediaType());
        }).start();
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
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TapDefaultDataView<TAPUploadFileResponse> uploadView = new TapDefaultDataView<TAPUploadFileResponse>() {
            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                super.onSuccess(response, localID);
                saveImageToCacheAndSendMessage(context, roomID, bitmap, encodedThumbnail, messageModel.copyMessageModel(), response);
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                new Thread(() -> {
                    Uri imageUri = Uri.parse(imageData.getFileUri());
                    if (null != messageModel.getData() && null != imageUri.getScheme() && imageUri.getScheme().contains("content")) {
                        messageModel.getData().put(FILE_URI, TAPFileUtils.getInstance().getFilePath(context, imageUri));
                    }
                    messageUploadFailed(context, messageModel, roomID);
                    Intent intent = new Intent(UploadFailed);
                    intent.putExtra(UploadLocalID, localID);
                    intent.putExtra(UploadFailedErrorMessage, errorMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }).start();
            }
        };

        // Upload file
        TAPDataManager.getInstance()
                .uploadImage(localID, imageFile,
                        messageModel.getRoom().getRoomID(), imageData.getCaption(),
                        mimeType, uploadCallbacks, uploadView);
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
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TapDefaultDataView<TAPUploadFileResponse> uploadView = new TapDefaultDataView<TAPUploadFileResponse>() {

            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                sendFileMessageAfterUploadSuccess(context, roomID, videoFile.getName(), mimeType, messageModel.copyMessageModel(), response);
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                messageUploadFailed(context, messageModel, roomID);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, localID);
                intent.putExtra(UploadFailedErrorMessage, errorMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        };

        // Upload file
        TAPDataManager.getInstance()
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
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
            }
        };

        TapDefaultDataView<TAPUploadFileResponse> uploadView = new TapDefaultDataView<TAPUploadFileResponse>() {
            @Override
            public void onSuccess(TAPUploadFileResponse response, String localID) {
                super.onSuccess(response, localID);
                sendFileMessageAfterUploadSuccess(context, roomID, file.getName(), mimeType, messageModel.copyMessageModel(), response);
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                onError(error.getMessage(), localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                messageUploadFailed(context, messageModel, roomID);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, localID);
                intent.putExtra(UploadFailedErrorMessage, errorMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        };

        // Upload file
        TAPDataManager.getInstance()
                .uploadFile(localID, file,
                        messageModel.getRoom().getRoomID(),
                        mimeType, uploadCallbacks, uploadView);
    }

    private void messageUploadFailed(Context context, TAPMessageModel messageModelWithUri, String roomID) {
        if (TAPChatManager.getInstance().checkMessageIsUploading(messageModelWithUri.getLocalID())) {
            TAPChatManager.getInstance().removeUploadingMessageFromHashMap(messageModelWithUri.getLocalID());
            getBitmapQueue().remove(messageModelWithUri.getLocalID());
            cancelUpload(context, messageModelWithUri, roomID);
            messageModelWithUri.setSending(false);
            messageModelWithUri.setFailedSend(true);
            TAPDataManager.getInstance().insertToDatabase(TAPChatManager.getInstance().convertToEntity(messageModelWithUri));
        }
    }

    /**
     * Modify image before uploading
     */
    public void createAndResizeImageFile(Context context, Uri imageUri, int imageMaxSize, BitmapInterface bitmapInterface) {
        final String mimeType = TAPUtils.getInstance().getImageMimeType(context, imageUri);
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
            bitmap = compressBitmap(bitmap, mimeType, IMAGE_COMPRESSION_QUALITY);
            bitmapInterface.onBitmapReady(bitmap);
        }).start();
    }

    private void createAndResizeImageFile(Bitmap bitmap, int imageMaxSize, BitmapInterface bitmapInterface) {
        new Thread(() -> {
            Bitmap bitmapEdit = bitmap;
            bitmapEdit = resizeBitmap(bitmapEdit, imageMaxSize);
            // TODO: 3 May 2019 MIME TYPE FORCED TO JPEG
            bitmapEdit = compressBitmap(bitmapEdit, IMAGE_JPEG, IMAGE_COMPRESSION_QUALITY);
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
            pathName = TAPFileUtils.getInstance().getFilePath(TapTalk.appContext, imageUri);
        } else {
            pathName = imageUri.toString();
        }

        int orientation = TAPFileUtils.getInstance().getImageOrientation(pathName);
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
        //ini ngecek kalau kosong ga perlu jalanin lagi
        if ((!isUploadQueueEmpty(roomID) || 0 < getUploadQueue(roomID).size())
                && null != getUploadQueue(roomID).get(0)
                && TAPDefaultConstant.MessageType.TYPE_IMAGE == getUploadQueue(roomID).get(0).getType()) {
            uploadImage(context, roomID);
        } else if ((!isUploadQueueEmpty(roomID) || 0 < getUploadQueue(roomID).size())
                && null != getUploadQueue(roomID).get(0)
                && TAPDefaultConstant.MessageType.TYPE_VIDEO == getUploadQueue(roomID).get(0).getType()) {
            uploadVideo(context, roomID);
        } else if ((!isUploadQueueEmpty(roomID) || 0 < getUploadQueue(roomID).size())
                && null != getUploadQueue(roomID).get(0)
                && TAPDefaultConstant.MessageType.TYPE_FILE == getUploadQueue(roomID).get(0).getType()) {
            uploadFile(context, roomID);
        }
    }

    /**
     * @param cancelledMessageModel
     */
    public void cancelUpload(Context context, TAPMessageModel cancelledMessageModel, String roomID) {
        int position = TAPUtils.getInstance().searchMessagePositionByLocalID(getUploadQueue(roomID), cancelledMessageModel.getLocalID());
        removeUploadProgressMap(cancelledMessageModel.getLocalID());
        getBitmapQueue().remove(cancelledMessageModel.getLocalID());
        if (-1 != position && 0 == position && !isUploadQueueEmpty(roomID)) {
            TAPDataManager.getInstance().unSubscribeToUploadImage(roomID);
            uploadNextSequence(context, roomID);
        } else if (-1 != position && !isUploadQueueEmpty(roomID)) {
            getUploadQueue(roomID).remove(position);
        }
    }

    private void saveImageToCacheAndSendMessage(Context context, String roomID, Bitmap bitmap,
                                                String encodedThumbnail, TAPMessageModel messageModel,
                                                TAPUploadFileResponse response) {
        try {
            //add ke dalem cache
            new Thread(() -> {
                try {
                    String fileID = response.getId();
                    TAPCacheManager.getInstance(context).addBitmapDrawableToCache(fileID, new BitmapDrawable(context.getResources(), bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            String localID = messageModel.getLocalID();
            long size = 0L;
            if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                size = ((Number) messageModel.getData().get(SIZE)).longValue();
            }
            addUploadProgressMap(localID, 100, size);
            TAPDataImageModel imageDataModel = TAPDataImageModel.Builder(response.getId(), encodedThumbnail,
                    response.getMediaType(), response.getSize(), response.getWidth(),
                    response.getHeight(), response.getCaption());
            HashMap<String, Object> imageDataMap = imageDataModel.toHashMapWithoutFileUri();
            if (null != messageModel.getData()) {
                messageModel.putData(imageDataMap);
                //messageModel.getData().remove(FILE_URI);
            } else {
                messageModel.setData(imageDataMap);
            }

            new Thread(() -> TAPChatManager.getInstance().sendImageMessageToServer(messageModel)).start();

            //removeUploadProgressMap(localID);
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadImageData, imageDataMap);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            //manggil restart buat queue selanjutnya
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
            long size = 0L;
            if (null != messageModel.getData() && null != messageModel.getData().get(SIZE)) {
                size = ((Number) messageModel.getData().get(SIZE)).longValue();
            }
            addUploadProgressMap(localID, 100, size);

            TAPFileDownloadManager.getInstance().saveFileMessageUri(roomID, response.getId(), (String) messageModel.getData().get(FILE_URI));
            TAPDataFileModel fileDataModel = TAPDataFileModel.Builder(response.getId(), fileName,
                    mimetype, response.getSize());
            HashMap<String, Object> fileDataMap = fileDataModel.toHashMap();
            if (null != messageModel.getData()) {
                messageModel.putData(fileDataMap);
                messageModel.getData().remove(FILE_URI);
            } else {
                messageModel.setData(fileDataMap);
            }

            new Thread(() -> TAPChatManager.getInstance().sendFileMessageToServer(messageModel)).start();

            //removeUploadProgressMap(localID);
            Intent intent = new Intent(UploadProgressFinish);
            intent.putExtra(UploadLocalID, localID);
            intent.putExtra(UploadFileData, fileDataMap);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            //manggil restart buat queue selanjutnya
            uploadNextSequence(context, roomID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSizeAllowedForUpload(long size) {
        return maxUploadSize >= size;
    }

    public void resetFileUploadManager() {
        getUploadProgressMapBytes().clear();
        getUploadProgressMapPercent().clear();
        getBitmapQueue().clear();
        getUploadQueuePerRoom().clear();
    }
}

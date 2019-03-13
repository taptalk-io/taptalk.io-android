package io.taptalk.TapTalk.Manager;

import android.content.ContentResolver;
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
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataFileModel;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailedErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;

public class TAPFileUploadManager {

    private final String TAG = TAPFileUploadManager.class.getSimpleName();
    private static TAPFileUploadManager instance;
    private HashMap<String, List<TAPMessageModel>> uploadQueuePerRoom;
    private HashMap<String, Bitmap> bitmapQueue; // Used for sending images with bitmap
    private HashMap<String, Integer> uploadProgressMap;
    //max Size for upload file message
    private long maxSize = 25 * 1024 * 1024;
    private HashMap<String, String> fileProviderPathMap;

    private TAPFileUploadManager() {
    }

    public static TAPFileUploadManager getInstance() {
        return null == instance ? instance = new TAPFileUploadManager() : instance;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    private HashMap<String, Integer> getUploadProgressMap() {
        return null == uploadProgressMap ? uploadProgressMap = new LinkedHashMap<>() : uploadProgressMap;
    }

    public Integer getUploadProgressMapProgressPerLocalID(String localID) {
        if (!getUploadProgressMap().containsKey(localID)) {
            return null;
        } else {
            return getUploadProgressMap().get(localID);
        }
    }

    public void addUploadProgressMap(String localID, int progress) {
        getUploadProgressMap().put(localID, progress);
    }

    public void removeUploadProgressMap(String localID) {
        getUploadProgressMap().remove(localID);
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

    private HashMap<String, String> getFileProviderPathMap() {
        return null == fileProviderPathMap ? fileProviderPathMap = new HashMap<>() : fileProviderPathMap;
    }

    public void addFileProviderPath(Uri fileProviderUri, String path) {
        getFileProviderPathMap().put(fileProviderUri.toString(), path);
    }

    public String getFileProviderPath(Uri fileProviderUri) {
        return getFileProviderPathMap().get(fileProviderUri.toString());
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
        } else if (1 == getUploadQueueSize(roomID) && TAPDefaultConstant.MessageType.TYPE_FILE == messageModel.getType()) {
            uploadFile(context, roomID);
        }
    }

    public void addUploadQueue(Context context, String roomID, TAPMessageModel messageModel, Bitmap bitmap) {
        getBitmapQueue().put(messageModel.getLocalID(), bitmap);
        addUploadQueue(context, roomID, messageModel);
    }

    // TODO: 14 January 2019 HANDLE OTHER FILE TYPES
    private void uploadImage(Context context, String roomID) {
        new Thread(() -> {
            if (isUploadQueueEmpty(roomID)) {
                // Queue is empty
                return;
            }

            TAPMessageModel messageModel = getUploadQueue(roomID).get(0);

            if (null == messageModel.getData()) {
                // No data
                Log.e(TAG, context.getString(R.string.tap_error_image_data_empty));
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_image_data_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }
            TAPDataImageModel imageData = new TAPDataImageModel(messageModel.getData());

            // Create and resize image file
            Uri imageUri = null;
            Bitmap bitmap, thumbBitmap;
            String thumbBase64;
            if (null != imageData.getFileUri() && !imageData.getFileUri().isEmpty()) {
                // Create image from file URI
                imageUri = Uri.parse(imageData.getFileUri());
                bitmap = createAndResizeImageFile(context, imageUri, IMAGE_MAX_DIMENSION);
                thumbBitmap = createAndResizeImageFile(context, imageUri, THUMB_MAX_DIMENSION);
                thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(thumbBitmap);
            } else if (null != getBitmapQueue().get(messageModel.getLocalID())) {
                // Create image from bitmap queue
                bitmap = getBitmapQueue().get(messageModel.getLocalID());
                bitmap = createAndResizeImageFile(bitmap, IMAGE_MAX_DIMENSION);
                thumbBitmap = createAndResizeImageFile(bitmap, THUMB_MAX_DIMENSION);
                thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(thumbBitmap);
            } else {
                // Image data does not contain URI
                Log.e(TAG, context.getString(R.string.tap_error_image_uri_empty));
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_image_uri_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }

            if (null != bitmap) {
                ContentResolver cr = context.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String mimeType = null == imageUri ? IMAGE_JPEG : cr.getType(imageUri);
                String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
                File imageFile = TAPUtils.getInstance().createTempFile(mimeTypeExtension, bitmap);

                // Update message data
                imageData.setHeight(bitmap.getHeight());
                imageData.setWidth(bitmap.getWidth());
                imageData.setSize(imageFile.length());
                imageData.setMediaType(mimeType);
                imageData.setThumbnail(thumbBase64);

                messageModel.putData(imageData.toHashMap());
                callImageUploadAPI(context, roomID, messageModel, imageFile, bitmap, thumbBase64, mimeType, imageData);
            }
        }).start();
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
                Log.e(TAG, context.getString(R.string.tap_error_image_data_empty));
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_image_data_empty));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }
            TAPDataFileModel fileData = new TAPDataFileModel(messageModel.getData());
            Uri fileUri = TAPFileDownloadManager.getInstance().getFileMessageUri(roomID, messageModel.getLocalID());
            Log.e(TAG, "uploadFile: " + fileUri);

            if (null == fileUri) {
                // File URI not found
                getUploadQueue(roomID).remove(0);
                getBitmapQueue().remove(messageModel.getLocalID());
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, context.getString(R.string.tap_error_image_uri_not_found));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }

            String pathName = TAPFileUtils.getInstance().getFilePath(context, fileUri);
            Log.e(TAG, "uploadFile: " + pathName);
            File tempFile = new File(pathName);
            callFileUploadAPI(context, roomID, messageModel, tempFile, fileData.getMediaType());
        }).start();
    }

    private void callImageUploadAPI(Context context, String roomID, TAPMessageModel messageModel, File imageFile,
                                    Bitmap bitmap, String encodedThumbnail, String mimeType,
                                    TAPDataImageModel imageData) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                addUploadProgressMap(localID, percentage);
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
                messageUploadFailed(context, messageModel, roomID);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, localID);
                intent.putExtra(UploadFailedErrorMessage, error.getMessage());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
                .uploadImage(localID, imageFile,
                        messageModel.getRoom().getRoomID(), imageData.getCaption(),
                        mimeType, uploadCallbacks, uploadView);
    }

    private void callFileUploadAPI(Context context, String roomID, TAPMessageModel messageModel, File file, String mimeType) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                addUploadProgressMap(localID, percentage);
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
                sendFileMessageAfterUploadSuccess(context, roomID, file.getName(),
                        file.getAbsolutePath(), mimeType, messageModel.copyMessageModel(), response);
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                messageUploadFailed(context, messageModel, roomID);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, localID);
                intent.putExtra(UploadFailedErrorMessage, error.getMessage());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
        new Thread(() -> {
            if (TAPChatManager.getInstance().checkMessageIsUploading(messageModelWithUri.getLocalID())) {
                TAPChatManager.getInstance().removeUploadingMessageFromHashMap(messageModelWithUri.getLocalID());
                getBitmapQueue().remove(messageModelWithUri.getLocalID());
                cancelUpload(context, messageModelWithUri, roomID);
                messageModelWithUri.setSending(false);
                messageModelWithUri.setFailedSend(true);
                TAPDataManager.getInstance().insertToDatabase(TAPChatManager.getInstance().convertToEntity(messageModelWithUri));
            }
        }).start();
    }

    /**
     * Modify image before uploading
     */
    private Bitmap createAndResizeImageFile(Context context, Uri imageUri, int imageMaxSize) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            bitmap = resizeBitmap(bitmap, imageMaxSize);
            bitmap = fixImageOrientation(bitmap, imageUri);
            bitmap = compressBitmap(bitmap, IMAGE_COMPRESSION_QUALITY);
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private Bitmap createAndResizeImageFile(Bitmap bitmap, int imageMaxSize) {
        bitmap = resizeBitmap(bitmap, imageMaxSize);
        bitmap = compressBitmap(bitmap, IMAGE_COMPRESSION_QUALITY);
        return bitmap;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int imageMaxSize) {
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
        pathName = TAPFileUtils.getInstance().getFilePath(TapTalk.appContext, imageUri);
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

    private Bitmap compressBitmap(Bitmap bitmap, int compressionQuality) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, os);
            byte[] byteArray = os.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
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
            addUploadProgressMap(localID, 100);
            TAPDataImageModel imageDataModel = TAPDataImageModel.Builder(response.getId(), encodedThumbnail,
                    response.getMediaType(), response.getSize(), response.getWidth(),
                    response.getHeight(), response.getCaption());
            HashMap<String, Object> imageDataMap = imageDataModel.toHashMapWithoutFileUri();
            if (null != messageModel.getData()) {
                messageModel.putData(imageDataMap);
                messageModel.getData().remove(FILE_URI);
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

    private void sendFileMessageAfterUploadSuccess(Context context, String roomID, String fileName, String fileUri,
                                                   String mimetype, TAPMessageModel messageModel,
                                                   TAPUploadFileResponse response) {
        try {
            String localID = messageModel.getLocalID();
            addUploadProgressMap(localID, 100);

            TAPDataFileModel fileDataModel = TAPDataFileModel.Builder(response.getId(), fileName,
                    mimetype, response.getSize());
            HashMap<String, Object> fileDataMap = fileDataModel.toHashMap();
            if (null != messageModel.getData()) {
                messageModel.putData(fileDataMap);
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

    /**
     * Buat ngubah file length jadi format kb/mb/gb
     *
     * @param size = file.length
     * @return
     */
    public boolean isSizeBelowUploadMaximum(long size) {
        return maxSize >= size;
    }
}

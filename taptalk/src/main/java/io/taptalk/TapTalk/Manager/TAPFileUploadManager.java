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
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailedErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;

public class TAPFileUploadManager {

    private final String TAG = TAPFileUploadManager.class.getSimpleName();
    private static TAPFileUploadManager instance;
    private HashMap<String, List<TAPMessageModel>> uploadQueuePerRoom;
    private HashMap<String, Integer> uploadProgressMap;
    private HashMap<String, String> imagePathMap;

    private TAPFileUploadManager() {
    }

    public static TAPFileUploadManager getInstance() {
        return null == instance ? instance = new TAPFileUploadManager() : instance;
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

    private boolean isUploadQueueIsEmpty(String roomID) {
        if (!getUploadQueuePerRoom().containsKey(roomID) && null != getUploadQueue(roomID))
            return false;

        return getUploadQueue(roomID).isEmpty();
    }

    private HashMap<String, String> getImagePathMap() {
        return null == imagePathMap ? imagePathMap = new HashMap<>() : imagePathMap;
    }

    public void addImagePath(Uri uri, String path) {
        getImagePathMap().put(uri.toString(), path);
    }

    public String getImagePath(Uri uri) {
        return getImagePathMap().get(uri.toString());
    }

    /**
     * Masukin Message Model ke dalem Queue Upload Image
     *
     * @param roomID
     * @param messageModel
     */
    public void addQueueUploadImage(Context context, String roomID,
                                    TAPMessageModel messageModel) {
        addUploadQueue(messageModel);
        if (1 == getUploadQueueSize(roomID)) uploadImage(context, roomID);
    }

    // TODO: 14 January 2019 HANDLE OTHER FILE TYPES
    private void uploadImage(Context context, String roomID) {
        new Thread(() -> {
            if (isUploadQueueIsEmpty(roomID)) {
                // Queue is empty
                return;
            }

            TAPMessageModel messageModel = getUploadQueue(roomID).get(0);
            TAPMessageModel apiMessageModel = messageModel.copyMessageModel();

            Log.e(TAG, "startUploadSequenceFromQueue: " + messageModel.getData());
            if (null == messageModel.getData() || null == apiMessageModel.getData()) {
                // No data
                Log.e(TAG, "File upload failed: data is required in MessageModel.");
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, "File upload failed: data is required in MessageModel.");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }
            TAPDataImageModel imageData = new TAPDataImageModel(messageModel.getData());

            // Create and resize image file
            Uri imageUri;
            if (null == imageData.getFileUri()) {
                // Image data does not contain URI
                Log.e(TAG, "File upload failed: URI is required in MessageModel data.");
                getUploadQueue(roomID).remove(0);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, messageModel.getLocalID());
                intent.putExtra(UploadFailedErrorMessage, "File upload failed: URI is required in MessageModel data.");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            } else {
                imageUri = Uri.parse(imageData.getFileUri());
            }

            Bitmap thumbBitmap = createAndResizeImageFile(context, imageUri, THUMB_MAX_DIMENSION);
            String thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(thumbBitmap);

            Bitmap bitmap = createAndResizeImageFile(context, imageUri, IMAGE_MAX_DIMENSION);

            if (null != bitmap) {
                ContentResolver cr = context.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String mimeType = cr.getType(imageUri);
                String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
                File imageFile = TAPUtils.getInstance().createTempFile(mimeTypeExtension, bitmap);

                // Update message data
                imageData.setHeight(bitmap.getHeight());
                imageData.setWidth(bitmap.getWidth());
                imageData.setSize(imageFile.length());
                imageData.setMediaType(mimeType);
                imageData.setThumbnail(thumbBase64);

                messageModel.putData(imageData.toHashMap());
                apiMessageModel.putData(imageData.toHashMapWithoutFileUri());

                callUploadAPI(context, roomID, messageModel, apiMessageModel, imageFile, bitmap, thumbBase64, mimeType, imageData);
            }
        }).start();
    }

    private void callUploadAPI(Context context, String roomID, TAPMessageModel messageModelWithUri, TAPMessageModel messageModel, File imageFile,
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
                saveImageToCache(context, roomID, bitmap, encodedThumbnail, messageModel, response);
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                messageUploadFailed(context, messageModelWithUri, roomID);
                Intent intent = new Intent(UploadFailed);
                intent.putExtra(UploadLocalID, localID);
                intent.putExtra(UploadFailedErrorMessage, error.getMessage());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                Log.e(TAG, "onError: " );
                messageUploadFailed(context, messageModelWithUri, roomID);
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

    private void messageUploadFailed(Context context, TAPMessageModel messageModelWithUri, String roomID) {
        new Thread(() -> {
            if (TAPChatManager.getInstance().checkMessageIsUploading(messageModelWithUri.getLocalID())) {
                TAPChatManager.getInstance().removeUploadingMessageFromHashMap(messageModelWithUri.getLocalID());
                cancelUpload(context, messageModelWithUri, roomID);
                messageModelWithUri.setSending(false);
                messageModelWithUri.setFailedSend(true);
                TAPDataManager.getInstance().insertToDatabase(TAPChatManager.getInstance().convertToEntity(messageModelWithUri));
            }
        }).start();
    }

    private Bitmap createAndResizeImageFile(Context context, Uri imageUri, @NonNull int imageMaxSize) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            // Resize image
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

            // Fix image orientation
            String pathName;
            pathName = TAPFileUtils.getInstance().getFilePath(TapTalk.appContext, imageUri);
            int orientation = TAPFileUtils.getInstance().getImageOrientation(pathName);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            // Compress image
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, os);
            byte[] byteArray = os.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            os.flush();
            os.close();

            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Check upload queue and restart upload sequence
     */
    public void uploadNextSequence(Context context, String roomID) {
        getUploadQueue(roomID).remove(0);
        //ini ngecek kalau kosong ga perlu jalanin lagi
        if (!isUploadQueueIsEmpty(roomID) || 0 < getUploadQueue(roomID).size()) {
            Log.e(TAG, "uploadNextSequence: "+getUploadQueue(roomID).size() );
            uploadImage(context, roomID);
        }
    }

    /**
     * @param cancelledMessageModel
     */
    public void cancelUpload(Context context, TAPMessageModel cancelledMessageModel, String roomID) {
        int position = TAPUtils.getInstance().searchMessagePositionByLocalID(getUploadQueue(roomID), cancelledMessageModel.getLocalID());
        removeUploadProgressMap(cancelledMessageModel.getLocalID());
        if (-1 != position && 0 == position && !isUploadQueueIsEmpty(roomID)) {
            TAPDataManager.getInstance().unSubscribeToUploadImage(roomID);
            uploadNextSequence(context, roomID);
        } else if (-1 != position && !isUploadQueueIsEmpty(roomID)) {
            getUploadQueue(roomID).remove(position);
        }
    }

    private void saveImageToCache(Context context, String roomID, Bitmap bitmap, String encodedThumbnail, TAPMessageModel messageModel,
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package io.taptalk.TapTalk.Manager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPUploadListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;

public class TAPFileManager {

    private final String TAG = TAPFileManager.class.getSimpleName();
    private static TAPFileManager instance;
    private List<TAPMessageModel> uploadQueue;
    private HashMap<String, Integer> uploadProgressMap;

    private TAPFileManager() {
        uploadQueue = new ArrayList<>();
    }

    public static TAPFileManager getInstance() {
        return null == instance ? instance = new TAPFileManager() : instance;
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

    private void removeUploadProgressMap(String localID) {
        getUploadProgressMap().remove(localID);
    }

    /**
     * Masukin Message Model ke dalem Queue Upload Image
     *
     * @param messageModel
     */
    public void addQueueUploadImage(Context context,
                                    TAPMessageModel messageModel,
                                    TAPUploadListener uploadListener) {
        uploadQueue.add(messageModel);
        if (1 == uploadQueue.size()) uploadImage(context, uploadListener);
    }

    // TODO: 14 January 2019 HANDLE OTHER FILE TYPES
    private void uploadImage(Context context, TAPUploadListener uploadListener) {
        new Thread(() -> {
            Log.e(TAG, "startUploadSequenceFromQueue: ");
            if (uploadQueue.isEmpty()) {
                // Queue is empty
                return;
            }

            TAPMessageModel messageModel = uploadQueue.get(0);

            Log.e(TAG, "startUploadSequenceFromQueue: " + messageModel.getData());
            if (null == messageModel.getData()) {
                // No data
                Log.e(TAG, "File upload failed: data is required in MessageModel.");
                uploadQueue.remove(0);
                return;
            }
            TAPDataImageModel imageData = TAPUtils.getInstance().convertObject(messageModel.getData(),
                    new TypeReference<TAPDataImageModel>() {
                    });

            // Create and resize image file
            Uri imageUri = Uri.parse(imageData.getFileUri());
            if (null == imageUri) {
                // Image data does not contain URI
                Log.e(TAG, "File upload failed: URI is required in MessageModel data.");
                uploadQueue.remove(0);
                return;
            }

            Bitmap bitmap = createAndResizeImageFile(context, imageUri);

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
                messageModel.setData(imageData.toHashMapWithoutFileUri());

                callUploadAPI(context, messageModel, imageFile, mimeType, imageData, uploadListener);
            }
        }).start();
    }

    private void callUploadAPI(Context context, TAPMessageModel messageModel, File imageFile, String mimeType,
                               TAPDataImageModel imageData,
                               TAPUploadListener uploadListener) {

        String localID = messageModel.getLocalID();

        ProgressRequestBody.UploadCallbacks uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                addUploadProgressMap(localID, percentage);
                uploadListener.onProgressLoading(localID);
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
                Log.e(TAG, "onSuccess: " );
                removeUploadProgressMap(localID);
                uploadListener.onProgressFinish(localID);
                TAPDataManager.getInstance().removeUploadSubscriber();

                //manggil restart buat queue selanjutnya
                uploadNextSequence(context, uploadListener);
                // TODO: 10/01/19 send emit message to server
            }

            @Override
            public void onError(TAPErrorModel error, String localID) {
                uploadListener.onUploadFailed(localID);
            }

            @Override
            public void onError(String errorMessage, String localID) {
                uploadListener.onUploadFailed(localID);
            }
        };

        // Upload file
        TAPDataManager.getInstance()
                .uploadImage(localID, imageFile,
                        messageModel.getRoom().getRoomID(), imageData.getCaption(),
                        mimeType, uploadCallbacks, uploadView);
    }

    private Bitmap createAndResizeImageFile(Context context, Uri imageUri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            float scaleRatio = Math.min(
                    (float) IMAGE_MAX_DIMENSION / bitmap.getWidth(),
                    (float) IMAGE_MAX_DIMENSION / bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    Math.round(scaleRatio * bitmap.getWidth()),
                    Math.round(scaleRatio * bitmap.getHeight()),
                    false);

            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Check upload queue and restart upload sequence
     */
    public void uploadNextSequence(Context context,
                                   TAPUploadListener uploadListener) {
        uploadQueue.remove(0);
        //ini ngecek kalau kosong ga perlu jalanin lagi
        if (!uploadQueue.isEmpty()) {
            uploadImage(context, uploadListener);
        }
    }

    /**
     * @param cancelledMessageModel
     */
    public void cancelUpload(Context context, TAPMessageModel cancelledMessageModel,
                             TAPUploadListener uploadListener) {
        int position = TAPUtils.getInstance().searchMessagePositionByLocalID(uploadQueue, cancelledMessageModel.getLocalID());
        removeUploadProgressMap(cancelledMessageModel.getLocalID());
        if (-1 != position && 0 == position && !uploadQueue.isEmpty()) {
            uploadNextSequence(context, uploadListener);
        } else if (-1 != position && !uploadQueue.isEmpty()) {
            uploadQueue.remove(position);
        }
    }

    // TODO: 14 January 2019 REMOVE FROM QUEUE WHEN UPLOAD FINISHES
}

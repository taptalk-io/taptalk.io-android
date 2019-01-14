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
import java.util.List;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_MAX_DIMENSION;

public class TAPFileManager {

    private final String TAG = TAPFileManager.class.getSimpleName();
    private static TAPFileManager instance;
    private List<TAPMessageModel> uploadQueue;

    private TAPFileManager() {
        uploadQueue = new ArrayList<>();
    }

    public static TAPFileManager getInstance() {
        return null == instance ? instance = new TAPFileManager() : instance;
    }

    /**
     *
     * @param messageModel
     * Requires TAPMessageModel with data
     *
     * @param uploadCallback
     * Callback is used to obtain upload progress
     *
     */
    public void uploadImage(Context context, TAPMessageModel messageModel, ProgressRequestBody.UploadCallbacks uploadCallback, TapDefaultDataView<TAPUploadFileResponse> view) {
        uploadQueue.add(messageModel);
        if (uploadQueue.size() == 1) {
            // Start upload if queue is not running
            startUploadSequenceFromQueue(context, uploadCallback, view);
        }
    }

    // TODO: 14 January 2019 HANDLE OTHER FILE TYPES
    private void startUploadSequenceFromQueue(Context context, ProgressRequestBody.UploadCallbacks uploadCallback, TapDefaultDataView<TAPUploadFileResponse> view) {
        new Thread(() -> {
            if (uploadQueue.size() == 0) {
            // Queue is empty
            return;
            }
            TAPMessageModel messageModel = uploadQueue.get(0);
            if (null == messageModel.getData()) {
                // No data
                Log.e(TAG, "File upload failed: data is required in MessageModel.");
                uploadQueue.remove(messageModel);
                return;
            }
            TAPDataImageModel imageData = TAPUtils.getInstance().convertObject(messageModel.getData(), new TypeReference<TAPDataImageModel>() {});

            // Create and resize image file
            Uri imageUri = imageData.getFileUri();
            if (null == imageUri) {
                // Image data does not contain URI
                Log.e(TAG, "File upload failed: URI is required in MessageModel data.");
                return;
            }
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return;
            }
            float scaleRatio = Math.min(
                    (float) IMAGE_MAX_DIMENSION / bitmap.getWidth(),
                    (float) IMAGE_MAX_DIMENSION / bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    Math.round(scaleRatio * bitmap.getWidth()),
                    Math.round(scaleRatio * bitmap.getHeight()),
                    false);
            ContentResolver cr = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeType = cr.getType(imageUri);
            String mimeTypeExtension = mime.getExtensionFromMimeType(mimeType);
            File imageFile = TAPUtils.getInstance().createTempFile(mimeTypeExtension, bitmap);

            // Update message data


            // Upload file
            TAPDataManager.getInstance().uploadImage(messageModel.getLocalID(), imageFile, messageModel.getRoom().getRoomID(), imageData.getCaption(), mimeType, uploadCallback, view);
        });
    }

    // TODO: 14 January 2019 REMOVE FROM QUEUE WHEN UPLOAD FINISHES
}

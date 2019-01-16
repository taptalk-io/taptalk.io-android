package io.taptalk.TapTalk.Manager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDownloadListener;
import io.taptalk.TapTalk.Listener.TAPUploadListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

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
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            if (originalWidth <= IMAGE_MAX_DIMENSION && originalHeight <= IMAGE_MAX_DIMENSION) {
                // Image is smaller than max dimensions
                return bitmap;
            }
            // Resize image
            float scaleRatio = Math.min(
                    (float) IMAGE_MAX_DIMENSION / originalWidth,
                    (float) IMAGE_MAX_DIMENSION / originalHeight);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    Math.round(scaleRatio * originalWidth),
                    Math.round(scaleRatio * originalHeight),
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

    public void writeDownloadedFileToDisk(String localID, ResponseBody responseBody, TAPDownloadListener listener) {
        // TODO: 16 January 2019 FILE NAME IS CURRENTLY USING TIMESTAMP
        String filename = TAPTimeFormatter.getInstance().formatTime(System.currentTimeMillis(), "yyyyMMddHHmmssSSS") + ".jpeg";
        // TODO: 16 January 2019 FIX FILE DIRECTORY
        Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/saved_images");
        myDir.mkdirs();
        File file = new File (myDir, filename);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Log.e(TAG, "writeDownloadedFileToDisk: " + file.getAbsolutePath());
            Log.e(TAG, "writeDownloadedFileToDisk: " + file.length());
            listener.onWriteToStorageFinished(localID, file, bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        File downloadedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
//        BufferedSink sink;
//        try {
//            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
//            sink = Okio.buffer(Okio.sink(downloadedFile));
//            sink.writeAll(responseBody.source());
//            sink.close();
//
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.getAbsolutePath());
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.getName());
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.length());
//            listener.onWriteToStorageFinished(localID, file, bmp);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "writeDownloadedFileToDisk: " + e.getLocalizedMessage());
//        }
    }

//    public void writeDownloadedFileToDisk(ResponseBody responseBody) {
//        // TODO: 16 January 2019 FILE NAME IS CURRENTLY USING TIMESTAMP
//        String filename = TAPTimeFormatter.getInstance().formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss");
//        File downloadedFile = new File((TapTalk.appContext.getFilesDir()), filename);
//        Log.e(TAG, "writeDownloadedFileToDisk: " + TapTalk.appContext.getExternalFilesDir(null).toString());
//
//        InputStream is;
//        OutputStream os;
//
//        try {
//            byte[] fileReader = new byte[1024];
//            long fileSize = responseBody.contentLength();
//            long downloaded = 0;
//
//            is = responseBody.byteStream();
//            os = new FileOutputStream(downloadedFile);
//
//            while (true) {
//                int read = is.read(fileReader);
//                if (read == -1) {
//                    break;
//                }
//                os.write(fileReader, 0, read);
//                downloaded += read;
////                Log.e(TAG, "writeDownloadedFileToDisk: " + downloaded + " of " + fileSize);
//            }
//            os.flush();
//            os.close();
//            is.close();
//            Log.e(TAG, "writeDownloadedFileToDisk: " + downloaded + " of " + fileSize);
//
//            File file = new File(TapTalk.appContext.getFilesDir(), filename);
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.getAbsolutePath());
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.getName());
//            Log.e(TAG, "writeDownloadedFileToDisk: " + file.length());
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "writeDownloadedFileToDisk: " + e.getLocalizedMessage());
//        }
//    }
}

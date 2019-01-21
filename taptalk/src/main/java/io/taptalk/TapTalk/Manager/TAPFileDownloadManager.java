package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDownloadListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;
import okhttp3.ResponseBody;

public class TAPFileDownloadManager {

    private final String TAG = TAPFileDownloadManager.class.getSimpleName();
    private static TAPFileDownloadManager instance;

    public static TAPFileDownloadManager getInstance() {
        return null == instance ? instance = new TAPFileDownloadManager() : instance;
    }

    // TODO: 17 January 2019 ADD QUEUE, SHOW DOWNLOAD PROGRESS
    // TODO: 21 January 2019 DO NOT DOWNLOAD IF STORAGE PERMISSION IS DENIED
    public void downloadImage(Context context, TAPMessageModel message, TAPDownloadListener listener) {
        if (null == message.getData() || TAPUtils.getInstance().hasPermissions(context, )) {
            return;
        }
        String fileID = (String) message.getData().get("fileID");
        // Download thumbnail
//        TAPDataManager.getInstance().downloadThumbnail(message.getRoom().getRoomID(), fileID, new TapDefaultDataView<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody response) {
//                writeImageFileToDisk(context, message.getLocalID(), fileID, response, listener);
//            }
//
//            @Override
//            public void onError(TAPErrorModel error) {
//                Log.e(TAG, "onError: " + error.getMessage());
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                Log.e(TAG, "onError: " + throwable.getMessage());
//            }
//        });
        // Download image
        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), fileID, new TapDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                writeImageFileToDisk(context, message.getLocalID(), fileID, message.getCreated(), response, listener);
            }

            @Override
            public void onError(TAPErrorModel error) {
                Log.e(TAG, "onError: " + error.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError: " + throwable.getMessage());
            }
        });
    }

    private void writeImageFileToDisk(Context context, String localID, String fileID, Long timestamp, ResponseBody responseBody, TAPDownloadListener listener) {
        new Thread(() -> {
            String filename = TAPTimeFormatter.getInstance().formatTime(timestamp, "yyyyMMdd_HHmmssSSS") + ".jpeg";
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + TapTalk.appContext.getString(R.string.app_name));
            dir.mkdirs();
            File file = new File (dir, filename);
            if (file.exists()) {
                file.delete();
            }
            try {
                Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, out);
                out.flush();
                out.close();
                saveImageToCache(context, localID, fileID, bmp, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveImageToCache(Context context, String localID, String fileID, Bitmap bitmap, TAPDownloadListener listener) {
        try {
            new Thread(() -> {
                try {
                    TAPCacheManager.getInstance(context).addBitmapToCache(fileID, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            listener.onImageDownloadProcessFinished(localID, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

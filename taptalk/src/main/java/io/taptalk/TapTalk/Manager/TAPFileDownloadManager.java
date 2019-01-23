package io.taptalk.TapTalk.Manager;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    private HashMap<String, Integer> downloadProgressMap;

    public static TAPFileDownloadManager getInstance() {
        return null == instance ? instance = new TAPFileDownloadManager() : instance;
    }

    public HashMap<String, Integer> getDownloadProgressMap() {
        return null == downloadProgressMap ? downloadProgressMap = new LinkedHashMap<>() : downloadProgressMap;
    }

    public Integer getDownloadProgressMapProgressPerLocalID(String localID) {
        if (!getDownloadProgressMap().containsKey(localID)) {
            return null;
        } else {
            return getDownloadProgressMap().get(localID);
        }
    }

    public void addDownloadProgressMap(String localID, int progress) {
        getDownloadProgressMap().put(localID, progress);
    }

    public void removeDownloadProgressMap(String localID) {
        getDownloadProgressMap().remove(localID);
    }

    // TODO: 17 January 2019 ADD QUEUE, SHOW DOWNLOAD PROGRESS
    public void downloadImage(Context context, TAPMessageModel message, TAPDownloadListener listener) {
        // Return if message data is null or permission is not granted
        if (null == message.getData() || !TAPUtils.getInstance().hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e(TAG, "downloadImage: ");
            return;
        }

        TAPFileDownloadManager.getInstance().addDownloadProgressMap(message.getLocalID(), 0);

        String fileID = (String) message.getData().get("fileID");

        // Download image
        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), message.getLocalID(), fileID, new TapDefaultDataView<ResponseBody>() {
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
            File file = new File(dir, filename);
            if (file.exists()) {
                file.delete();
            }
            try {
                Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
                out.flush();
                out.close();
                saveImageToCacheAndCallListener(context, localID, fileID, bmp, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveImageToCacheAndCallListener(Context context, String localID, String fileID, Bitmap bitmap, TAPDownloadListener listener) {
        try {
            try {
                TAPCacheManager.getInstance(context).addBitmapToCache(fileID, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onImageDownloadProcessFinished(localID, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

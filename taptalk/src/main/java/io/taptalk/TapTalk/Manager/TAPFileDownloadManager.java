package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDownloadListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;
import okhttp3.ResponseBody;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;

public class TAPFileDownloadManager {

    private final String TAG = TAPFileDownloadManager.class.getSimpleName();
    private static TAPFileDownloadManager instance;
    private HashMap<String, Integer> downloadProgressMap;
    private ArrayList<String> failedDownloads;

    public static TAPFileDownloadManager getInstance() {
        return null == instance ? instance = new TAPFileDownloadManager() : instance;
    }

    private HashMap<String, Integer> getDownloadProgressMap() {
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

    public ArrayList<String> getFailedDownloads() {
        return null == failedDownloads ? failedDownloads = new ArrayList<>() : failedDownloads;
    }

    public boolean hasFailedDownloads() {
        if (null == failedDownloads) {
            return false;
        }
        return getFailedDownloads().size() > 0;
    }
    public void clearFailedDownloads() {
        if (null == failedDownloads) {
            return;
        }
        getFailedDownloads().clear();
    }

    public void addFailedDownload(String localID) {
        if (getFailedDownloads().contains(localID)) {
            return;
        }
        getFailedDownloads().add(localID);
    }

    public void removeFailedDownload(String localID) {
        getFailedDownloads().remove(localID);
    }


    public void downloadImage(Context context, TAPMessageModel message, TAPDownloadListener listener) {
        // Return if message data is null
        if (null == message.getData()) {
            return;
        }
        String localID = message.getLocalID();
        String fileID = (String) message.getData().get("fileID");

        TAPFileDownloadManager.getInstance().addDownloadProgressMap(localID, 0);

        // Download image
        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), localID, fileID, new TapDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                new Thread(() -> {
                    try {
                        Bitmap bitmap = getBitmapFromResponse(response);
                        saveImageToCacheAndCallListener(context, localID, fileID, bitmap, listener);
                    } catch (Exception e)  {
                        setDownloadFailed(localID, listener);
                        Log.e(TAG, "onSuccess exception: ", e);
                    }
                }).start();
            }

            @Override
            public void onError(TAPErrorModel error) {
                setDownloadFailed(localID, listener);
                Log.e(TAG, "onError: " + error.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                setDownloadFailed(localID, listener);
                Log.e(TAG, "onError: " + throwable.getMessage());
            }
        });
    }

    private void saveDownloadedThumbnailAndCallListener(ResponseBody responseBody, String fileID, TAPDownloadListener listener) {
        Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
        listener.onThumbnailDownloaded(fileID, bmp);
    }

    private Bitmap getBitmapFromResponse(ResponseBody responseBody) throws Exception {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
        out.flush();
        out.close();
        return bitmap;
    }

    private void writeImageFileToDisk(Long timestamp, Bitmap bitmap) {
        new Thread(() -> {
            String filename = TAPTimeFormatter.getInstance().formatTime(timestamp, "yyyyMMdd_HHmmssSSS") + ".jpeg";
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + TapTalk.appContext.getString(R.string.app_name));
            dir.mkdirs();
            File file = new File(dir, filename);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveImageToCacheAndCallListener(Context context, String localID, String fileID, Bitmap bitmap, TAPDownloadListener listener) {
        try {
            TAPCacheManager.getInstance(context).addBitmapDrawableToCache(fileID, new BitmapDrawable(context.getResources(), bitmap));
            removeDownloadProgressMap(localID);
            if (hasFailedDownloads()) {
                removeFailedDownload(localID);
            }
            listener.onImageDownloadProcessFinished(localID, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDownloadFailed(String localID, TAPDownloadListener listener) {
        Log.e(TAG, "setDownloadFailed: " + localID);
        removeDownloadProgressMap(localID);
        listener.onDownloadFailed(localID);
    }
}

package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Listener.TAPDownloadListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity;
import io.taptalk.Taptalk.R;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;

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

    public void downloadFile(Context context, TAPMessageModel message, TAPDownloadListener listener) {
        // Return if message data is null
        if (null == message.getData()) {
            return;
        }
        String localID = message.getLocalID();
        String fileID = (String) message.getData().get(FILE_ID);

        addDownloadProgressMap(localID, 0);

        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), localID, fileID, new TapDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                writeFileToDiskAndCallListener(context, message, response, listener);
            }

            @Override
            public void onError(TAPErrorModel error) {
                setDownloadFailed(localID, listener);
                Log.e(TAG, "onError: " + error.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                setDownloadFailed(localID, listener);
                Log.e(TAG, "onError: ", throwable);
            }

            @Override
            public void endLoading() {
                TAPDataManager.getInstance().removeDownloadSubscriber(localID);
            }
        });
    }

    public void downloadImage(Context context, TAPMessageModel message, TAPDownloadListener listener) {
        // Return if message data is null
        if (null == message.getData()) {
            return;
        }
        String localID = message.getLocalID();
        String fileID = (String) message.getData().get(FILE_ID);

        addDownloadProgressMap(localID, 0);

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

            @Override
            public void endLoading() {
                TAPDataManager.getInstance().removeDownloadSubscriber(localID);
            }
        });
    }

    public void cancelFileDownload(String localID) {
        TAPDataManager.getInstance().cancelFileDownload(localID);
        removeFailedDownload(localID);
        removeDownloadProgressMap(localID);
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

    private void writeFileToDiskAndCallListener(Context context, TAPMessageModel message, ResponseBody responseBody, TAPDownloadListener listener) {
        //new Thread(() -> {
            String localID = message.getLocalID();
            String filename;
            if (null != message.getData()) {
                filename = (String) message.getData().get(FILE_NAME);
            } else {
                filename = TAPTimeFormatter.getInstance().formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS");
            }
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + TapTalk.appContext.getString(R.string.app_name) + "/Files");
            dir.mkdirs();
            File file = new File(dir, filename);
            if (file.exists()) {
                file.delete();
            }
            try {
                // Write file to disk
                // TODO: 5 March 2019 CHECK STORAGE PERMISSION
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(responseBody.source());
                sink.close();

                // Remove message from progress map
                removeDownloadProgressMap(localID);
                if (hasFailedDownloads()) {
                    removeFailedDownload(localID);
                }

                // Trigger download success on listener
                listener.onFileDownloadProcessFinished(localID, FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file));
            } catch (Exception e) {
                e.printStackTrace();
                setDownloadFailed(localID, listener);
                Log.e(TAG, "writeFileToDiskAndCallListener: " + e);
            }
        //}).start();
    }

    public void writeImageFileToDisk(Long timestamp, Bitmap bitmap, TapTalkActionInterface listener) {
        new Thread(() -> {
            String filename = TAPTimeFormatter.getInstance().formatTime(timestamp, "yyyyMMdd_HHmmssSSS") + ".jpeg";
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + TapTalk.appContext.getString(R.string.app_name) + "/Images");
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
                listener.onSuccess("Successfully saved " + filename);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure(e.getMessage());
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
        removeDownloadProgressMap(localID);
        listener.onDownloadFailed(localID);
    }
}

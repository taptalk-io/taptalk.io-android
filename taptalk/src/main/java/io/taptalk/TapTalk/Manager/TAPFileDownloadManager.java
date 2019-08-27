package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorCode;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IMAGE_COMPRESSION_QUALITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_PNG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPFileDownloadManager {

    private final String TAG = TAPFileDownloadManager.class.getSimpleName();
    private static TAPFileDownloadManager instance;
    private HashMap<String, Integer> downloadProgressMapPercent;
    private HashMap<String, Long> downloadProgressMapBytes;
    private HashMap<String, String> fileProviderPathMap; // Contains FileProvider Uri as key and file pathname as value
    private HashMap<String /*roomID*/, HashMap<String /*fileID*/, String /*stringUri*/>> fileMessageUriMap;
    private ArrayList<String> failedDownloads;

    public static TAPFileDownloadManager getInstance() {
        return null == instance ? instance = new TAPFileDownloadManager() : instance;
    }

    private HashMap<String, Integer> getDownloadProgressMapPercent() {
        return null == downloadProgressMapPercent ? downloadProgressMapPercent = new LinkedHashMap<>() : downloadProgressMapPercent;
    }

    private HashMap<String, Long> getDownloadProgressMapBytes() {
        return null == downloadProgressMapBytes ? downloadProgressMapBytes = new LinkedHashMap<>() : downloadProgressMapBytes;
    }

    public Integer getDownloadProgressPercent(String localID) {
        if (!getDownloadProgressMapPercent().containsKey(localID)) {
            return null;
        } else {
            return getDownloadProgressMapPercent().get(localID);
        }
    }

    public Long getDownloadProgressBytes(String localID) {
        if (!getDownloadProgressMapBytes().containsKey(localID)) {
            return null;
        } else {
            return getDownloadProgressMapBytes().get(localID);
        }
    }

    public void addDownloadProgressMap(String localID, int progress, long bytes) {
        getDownloadProgressMapPercent().put(localID, progress);
        getDownloadProgressMapBytes().put(localID, bytes);
    }

    public void removeDownloadProgressMap(String localID) {
        getDownloadProgressMapPercent().remove(localID);
        getDownloadProgressMapBytes().remove(localID);
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

    public void downloadFile(Context context, TAPMessageModel message) {
        // Return if message data is null
        if (null == message.getData()) {
            return;
        }
        
        String localID = message.getLocalID();
        String fileID = (String) message.getData().get(FILE_ID);
        Number fileSize = (Number) message.getData().get(SIZE);

        // Return if same download ID is already in progress
        if (null != getDownloadProgressPercent(localID)) {
            return;
        }

        addDownloadProgressMap(localID, 0, 0);

        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), localID, fileID, fileSize, new TAPDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                writeFileToDiskAndSendBroadcast(context, message, response);
            }

            @Override
            public void onError(TAPErrorModel error) {
                setDownloadFailed(localID, error.getCode(), error.getMessage());
                Log.e(TAG, "onError: " + error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                setDownloadFailed(localID, ERROR_CODE_OTHERS, errorMessage);
                Log.e(TAG, "onError: " + errorMessage);
            }

            @Override
            public void endLoading() {
                TAPDataManager.getInstance().removeDownloadSubscriber(localID);
            }
        });
    }

    public void downloadImage(Context context, TAPMessageModel message) {
        // Return if message data is null
        if (null == message.getData()) {
            return;
        }

        String localID = message.getLocalID();
        String fileID = (String) message.getData().get(FILE_ID);
        Number fileSize = (Number) message.getData().get(SIZE);

        // Return if same download ID is already in progress
        if (null != getDownloadProgressPercent(localID)) {
            return;
        }

        addDownloadProgressMap(localID, 0, 0);

        // Download image
        TAPDataManager.getInstance().downloadFile(message.getRoom().getRoomID(), localID, fileID, fileSize, new TAPDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                new Thread(() -> {
                    try {
                        String mimeType = (String) message.getData().get(MEDIA_TYPE);
                        Log.e(TAG, "mimeType: " + mimeType);
                        Bitmap bitmap = getBitmapFromResponse(response, null == mimeType ? IMAGE_JPEG : mimeType);
                        saveImageToCacheAndSendBroadcast(context, localID, fileID, bitmap);
                    } catch (Exception e) {
                        setDownloadFailed(localID, ERROR_CODE_OTHERS, e.getMessage());
                        Log.e(TAG, "onSuccess exception: ", e);
                    }
                }).start();
            }

            @Override
            public void onError(TAPErrorModel error) {
                setDownloadFailed(localID, error.getCode(), error.getMessage());
                Log.e(TAG, "onError: " + error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                setDownloadFailed(localID, ERROR_CODE_OTHERS, errorMessage);
                Log.e(TAG, "onError: " + errorMessage);
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

    private Bitmap getBitmapFromResponse(ResponseBody responseBody, String mimeType) throws Exception {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(mimeType.equals(IMAGE_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
        Log.e(TAG, "getBitmapFromResponse: " + (mimeType.equals(IMAGE_PNG) ? "Bitmap.CompressFormat.PNG" : "Bitmap.CompressFormat.JPEG"));
        out.flush();
        out.close();
        return bitmap;
    }

    private void writeFileToDiskAndSendBroadcast(Context context, TAPMessageModel message, ResponseBody responseBody) {
        String localID = message.getLocalID();
        String filename;
        if (null != message.getData() && null != message.getData().get(FILE_NAME)) {
            filename = (String) message.getData().get(FILE_NAME);
        } else if (null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
            String mimeType = (String) message.getData().get(MEDIA_TYPE);
            String extension = null == mimeType ? "" : mimeType.substring(mimeType.lastIndexOf("/") + 1);
            filename = TAPTimeFormatter.getInstance().formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS") + extension;
        } else {
            filename = TAPTimeFormatter.getInstance().formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS");
        }
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + TapTalk.appContext.getString(R.string.tap_app_name) + (message.getType() == TYPE_VIDEO ? "/" + TapTalk.appContext.getString(R.string.tap_app_name) + "Videos" :
                "/" + TapTalk.appContext.getString(R.string.tap_app_name) + " Files"));
        dir.mkdirs();

        File noMediaFile = new File(dir, ".nomedia");
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(dir, filename);
        file = TAPFileUtils.getInstance().renameDuplicateFile(file);
        try {
            // Write file to disk
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(responseBody.source());
            sink.close();

            // Remove message from progress map
            removeDownloadProgressMap(localID);
            if (hasFailedDownloads()) {
                removeFailedDownload(localID);
            }

            // Send download success broadcast
            Uri fileProviderUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);
            String fileID = (String) message.getData().get(FILE_ID);
            addFileProviderPath(fileProviderUri, file.getAbsolutePath());
            scanFile(context, file, TAPUtils.getInstance().getFileMimeType(file));
            Intent intent = new Intent(DownloadFinish);
            intent.putExtra(DownloadLocalID, localID);
            intent.putExtra(DownloadedFile, file);
            intent.putExtra(FILE_ID, fileID);
            intent.putExtra(FILE_URI, fileProviderUri);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            if (null != fileID && null != fileProviderUri) {
                TAPFileDownloadManager.getInstance().saveFileMessageUri(message.getRoom().getRoomID(), fileID, fileProviderUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDownloadFailed(localID, ERROR_CODE_OTHERS, e.getMessage());
            Log.e(TAG, "writeFileToDiskAndSendBroadcast: " + e);
        }
    }

    public void writeImageFileToDisk(Context context, Long timestamp, Bitmap bitmap, String mimeType, TapTalkActionInterface listener) {
        new Thread(() -> {
            String imageFormat = mimeType.equals(IMAGE_PNG) ? ".png" : ".jpeg";
            String filename = TAPTimeFormatter.getInstance().formatTime(timestamp, "yyyyMMdd_HHmmssSSS") + imageFormat;
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + TapTalk.appContext.getString(R.string.tap_app_name));
            dir.mkdirs();

            File file = new File(dir, filename);
            if (file.exists()) {
                file.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(mimeType.equals(IMAGE_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
                out.flush();
                out.close();
                scanFile(context, file, TAPUtils.getInstance().getFileMimeType(file));
                listener.onSuccess("Successfully saved " + filename);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            }
        }).start();
    }

    public void writeFileToDisk(Context context, TAPMessageModel message, TapTalkActionInterface listener) {
        new Thread(() -> {
            try {
                String filename;
                if (null != message.getData() && null != message.getData().get(FILE_NAME)) {
                    filename = (String) message.getData().get(FILE_NAME);
                } else if (null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
                    String mimeType = (String) message.getData().get(MEDIA_TYPE);
                    String extension = null == mimeType ? "" : mimeType.substring(mimeType.lastIndexOf("/") + 1);
                    filename = TAPTimeFormatter.getInstance().formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS") + extension;
                } else {
                    filename = TAPTimeFormatter.getInstance().formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS");
                }

                File dir = new File(TYPE_VIDEO == message.getType() ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + appContext.getString(R.string.tap_app_name)
                        : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
                dir.mkdirs();

                File targetFile = new File(dir, filename);
                targetFile = TAPFileUtils.getInstance().renameDuplicateFile(targetFile);

                if (null != message.getData() && null != message.getData().get(FILE_ID) &&
                        null != getFileMessageUri(message.getRoom().getRoomID(), (String) message.getData().get(FILE_ID))) {
                    File sourceFile = new File(getFileProviderPath(getFileMessageUri(message.getRoom().getRoomID(), (String) message.getData().get(FILE_ID))));
                    if (sourceFile.exists()) {
                        copyFile(sourceFile, targetFile);
                        scanFile(context, targetFile, TAPUtils.getInstance().getFileMimeType(targetFile));
                        listener.onSuccess("Successfully saved " + filename);
                    } else {
                        listener.onError(context.getString(R.string.tap_error_could_not_find_file));
                    }
                } else {
                    listener.onError(context.getString(R.string.tap_error_could_not_find_file));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "writeFileToDisk: ", e);
                listener.onError(e.getMessage());
            }
        }).start();
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    private void saveImageToCacheAndSendBroadcast(Context context, String localID, String fileID, Bitmap bitmap) {
        try {
            TAPCacheManager.getInstance(context).addBitmapDrawableToCache(fileID, new BitmapDrawable(context.getResources(), bitmap));
            removeDownloadProgressMap(localID);
            if (hasFailedDownloads()) {
                removeFailedDownload(localID);
            }
            Intent intent = new Intent(DownloadFinish);
            intent.putExtra(DownloadLocalID, localID);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDownloadFailed(String localID, String errorCode, String errorMessage) {
        addFailedDownload(localID);
        removeDownloadProgressMap(localID);
        Intent intent = new Intent(DownloadFailed);
        intent.putExtra(DownloadLocalID, localID);
        intent.putExtra(DownloadErrorCode, errorCode);
        intent.putExtra(DownloadErrorMessage, errorMessage);
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }

    public void getFileProviderPathFromPreference() {
        HashMap<String, String> filePathMap = TAPDataManager.getInstance().getFileProviderPathMap();
        if (null != filePathMap) {
            getFileProviderPathMap().putAll(filePathMap);
        }
    }

    public void saveFileProviderPathToPreference() {
        if (getFileProviderPathMap().size() > 0) {
            TAPDataManager.getInstance().saveFileProviderPathMap(getFileProviderPathMap());
        }
    }

    private HashMap<String, String> getFileProviderPathMap() {
        return null == fileProviderPathMap ? fileProviderPathMap = new HashMap<>() : fileProviderPathMap;
    }

    public String getFileProviderPath(Uri fileProviderUri) {
        return getFileProviderPathMap().get(fileProviderUri.toString());
    }

    public void addFileProviderPath(Uri fileProviderUri, String path) {
        getFileProviderPathMap().put(fileProviderUri.toString(), path);
    }

    private HashMap<String, HashMap<String, String>> getFileMessageUriMap() {
        return null == fileMessageUriMap ? fileMessageUriMap = new LinkedHashMap<>() : fileMessageUriMap;
    }

    public void getFileMessageUriFromPreference() {
        HashMap<String, HashMap<String, String>> fileUriMap = TAPDataManager.getInstance().getFileMessageUriMap();
        if (null != fileUriMap) {
            getFileMessageUriMap().putAll(fileUriMap);
        }
    }

    public void saveFileMessageUriToPreference() {
        TAPDataManager.getInstance().saveFileMessageUriMap(getFileMessageUriMap());
    }

    public Uri getFileMessageUri(String roomID, String fileID) {
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        if (null != roomUriMap && null != roomUriMap.get(fileID)) {
            return Uri.parse(roomUriMap.get(fileID));
        } else {
            return null;
        }
    }

    public void saveFileMessageUri(String roomID, String fileID, Uri fileUri) {
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        if (null != roomUriMap) {
            roomUriMap.put(fileID, fileUri.toString());
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(fileID, fileUri.toString());
            getFileMessageUriMap().put(roomID, hashMap);
        }
    }

    public void saveFileMessageUri(String roomID, String fileID, String filePath) {
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        if (null != roomUriMap) {
            roomUriMap.put(fileID, filePath);
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(fileID, filePath);
            getFileMessageUriMap().put(roomID, hashMap);
        }
    }

    public void removeFileMessageUri(String roomID, String fileID) {
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        if (null != roomUriMap) {
            roomUriMap.remove(fileID);
        }
    }

    private void scanFile(Context context, File f, String mimeType) {
        MediaScannerConnection
                .scanFile(context, new String[]{f.getAbsolutePath()},
                        new String[]{mimeType}, null);
    }

    public boolean checkPhysicalFileIsExist(TAPMessageModel message) {
        return (null != message.getData() && null != message.getData().get(FILE_ID) &&
                null != getFileMessageUri(message.getRoom().getRoomID(),
                (String) message.getData().get(FILE_ID)) && null != getFileProviderPath(getFileMessageUri(message.getRoom().getRoomID(),
                (String) message.getData().get(FILE_ID))) &&
                new File(getFileProviderPath(getFileMessageUri(message.getRoom().getRoomID(),
                (String) message.getData().get(FILE_ID)))).exists()) || (null != message.getData() && null != message.getData().get(FILE_ID) &&
                null != getFileMessageUri(message.getRoom().getRoomID(),
                        (String) message.getData().get(FILE_ID)) && new File(getFileMessageUri(message.getRoom().getRoomID(),
                (String) message.getData().get(FILE_ID)).getPath()).exists());
    }

    public void resetTAPFileDownloadManager() {
        getFileProviderPathMap().clear();
        getFileMessageUriMap().clear();
        getDownloadProgressMapBytes().clear();
        getDownloadProgressMapPercent().clear();
        getFailedDownloads().clear();
    }
}

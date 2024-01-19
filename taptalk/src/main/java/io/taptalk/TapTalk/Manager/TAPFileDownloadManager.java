package io.taptalk.TapTalk.Manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import io.taptalk.TapTalk.R;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorCode;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.AUDIO_MP3;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_PNG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.VIDEO_MP4;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPFileDownloadManager {

    private final String TAG = TAPFileDownloadManager.class.getSimpleName();
    private static HashMap<String, TAPFileDownloadManager> instances;

    private String instanceKey = "";
    private HashMap<String, Integer> downloadProgressMapPercent;
    private HashMap<String, Long> downloadProgressMapBytes;
    private HashMap<String, String> fileProviderPathMap; // Contains FileProvider Uri as key and file pathname as value
    private HashMap<String /*roomID*/, HashMap<String /*fileID*/, String /*stringUri*/>> fileMessageUriMap;
    private HashMap<String, DownloadFromUrlTask> downloadTaskMap;
    private ArrayList<String> failedDownloads;

    public static TAPFileDownloadManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPFileDownloadManager instance = new TAPFileDownloadManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPFileDownloadManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPFileDownloadManager(String instanceKey) {
        this.instanceKey = instanceKey;
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

    private String getFileNameFromMessage(TAPMessageModel message) {
        if (message == null) {
            return "";
        }
        String filename;
        if (null != message.getData() && null != message.getData().get(FILE_NAME)) {
            filename = (String) message.getData().get(FILE_NAME);
        }
        else {
            String mimeType = TAPUtils.getMimeTypeFromMessage(message);
            if (null != mimeType && !mimeType.isEmpty()) {
                String extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                filename = TAPTimeFormatter.formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS") + "." + extension;
            } else {
                filename = TAPTimeFormatter.formatTime(message.getCreated(), "yyyyMMdd_HHmmssSSS");
            }
        }
        return filename;
    }

    public void downloadMessageFile(TAPMessageModel message) {
        // Return if message data is null
        if (null == message || null == message.getData()) {
            return;
        }

        String localID = message.getLocalID();
        String fileUrl = (String) message.getData().get(FILE_URL);
        String fileID = (String) message.getData().get(FILE_ID);
        Number fileSize = (Number) message.getData().get(SIZE);

        // Return if same download ID is already in progress
        if (null != getDownloadProgressPercent(localID)) {
            return;
        }

        addDownloadProgressMap(localID, 0, 0);

        if (null != fileUrl && !fileUrl.isEmpty()) {
            downloadFileFromUrl(message, fileUrl);
        }
        else if (null != fileID && !fileID.isEmpty()) {
            TAPDataManager.getInstance(instanceKey).downloadFile(message.getRoom().getRoomID(), localID, fileID, fileSize, new TAPDefaultDataView<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody response) {
                    writeFileToDiskAndSendBroadcast(TapTalk.appContext, message, response);
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
                    TAPDataManager.getInstance(instanceKey).removeDownloadSubscriber(localID);
                }
            });
        }
    }

    private static class DownloadFromUrlTask extends AsyncTask<String, Integer, String> {
        private String instanceKey = "";
        private PowerManager.WakeLock wakeLock;
        private TAPMessageModel message;
        @Nullable private File file;
        @Nullable private Uri uri;
        private String urlString;

        public DownloadFromUrlTask(String instanceKey, TAPMessageModel message, Uri uri) {
            this.instanceKey = instanceKey;
            this.message = message;
            this.uri = uri;
        }
        public DownloadFromUrlTask(String instanceKey, TAPMessageModel message, File file) {
            this.instanceKey = instanceKey;
            this.message = message;
            this.file = file;
        }

        public String getLocalID() {
            return message.getLocalID();
        }

        @Override
        protected String doInBackground(String... urlStrings) {
            InputStream input;
            OutputStream output;
            HttpURLConnection connection;
            urlString = urlStrings[0];

            try {
                URL url = new URL(urlStrings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // Expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // This will be useful to display download percentage
                // Might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // Download the file
                input = connection.getInputStream();
                if (file != null) {
                    output = new FileOutputStream(file);
                }
                else {
                    output = appContext.getContentResolver().openOutputStream(uri);
                }

                byte[] data = new byte[4096];
                long bytesDownloaded = 0L;
                int count;
                while ((count = input.read(data)) != -1) {
                    // Download was cancelled
                    if (isCancelled()) {
                        TAPFileDownloadManager.getInstance(instanceKey).getDownloadTaskMap().remove(message.getLocalID());
                        input.close();
                        return null;
                    }
                    bytesDownloaded += count;
                    // Publish the progress if total length is known
                    if (fileLength > 0) {
                        final int downloadProgressPercent = (int) ((bytesDownloaded * 100L) / fileLength);
                        publishProgress(downloadProgressPercent);
                        TAPFileDownloadManager.getInstance(instanceKey).addDownloadProgressMap(message.getLocalID(), downloadProgressPercent, bytesDownloaded);
                        Intent intent = new Intent(DownloadProgressLoading);
                        intent.putExtra(DownloadLocalID, message.getLocalID());
                        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                        //Log.e("TapFileDownloadManager", "Download Progress: " + message.getLocalID() + " " + bytesDownloaded);
                    }
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Take CPU lock to prevent CPU from going off if the user presses the power button during download
            PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        @Override
        protected void onPostExecute(String result) {
            wakeLock.release();
            if (result != null) {
                TAPFileDownloadManager.getInstance(instanceKey).setDownloadFailed(message.getLocalID(), ERROR_CODE_OTHERS, appContext.getString(R.string.tap_error_message_general));
            } else {
                // Remove message from progress map
                TAPFileDownloadManager.getInstance(instanceKey).removeDownloadProgressMap(message.getLocalID());
                if (TAPFileDownloadManager.getInstance(instanceKey).hasFailedDownloads()) {
                    TAPFileDownloadManager.getInstance(instanceKey).removeFailedDownload(message.getLocalID());
                }
                TAPFileDownloadManager.getInstance(instanceKey).getDownloadTaskMap().remove(message.getLocalID());

                // Send download success broadcast
                Intent intent = new Intent(DownloadFinish);
                Uri broadcastUri;
                if (file != null) {
                    Uri fileProviderUri = FileProvider.getUriForFile(appContext, FILEPROVIDER_AUTHORITY, file);
                    TAPFileDownloadManager.getInstance(instanceKey).addFileProviderPath(fileProviderUri, file.getAbsolutePath());
                    TAPFileDownloadManager.getInstance(instanceKey).scanFile(appContext, file, TAPUtils.getFileMimeType(file));
                    intent.putExtra(DownloadedFile, file);
                    intent.putExtra(FILE_URI, fileProviderUri);
                    broadcastUri = fileProviderUri;
                }
                else {
                    String path = TAPFileUtils.getFilePath(appContext, uri);
                    if (path != null) {
                        TAPFileDownloadManager.getInstance(instanceKey).addFileProviderPath(uri, path);
                        intent.putExtra(DownloadedFile, new File(path));
                        intent.putExtra(FILE_URI, uri);
                    }
                    broadcastUri = uri;
                }

                intent.putExtra(DownloadLocalID, message.getLocalID());
                intent.putExtra(FILE_URL, urlString);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(
                    message.getRoom().getRoomID(),
                    TAPUtils.getUriKeyFromUrl(urlString),
                    broadcastUri
                );
            }
        }
    }

    private void downloadFileFromUrl(TAPMessageModel message, String fileUrl) {
        if (message.getData() == null) {
            return;
        }
        String filename = getFileNameFromMessage(message);
        File targetFile;
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (message.getType() == TYPE_FILE) {
                // Save to Downloads directory if type is file
                String mimeType = TAPUtils.getMimeTypeFromMessage(message);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + TapTalk.getClientAppName(instanceKey));
                ContentResolver contentResolver = appContext.getContentResolver();
                Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) {
                    return;
                }
                DownloadFromUrlTask downloadFromUrlTask = new DownloadFromUrlTask(instanceKey, message, uri);
                downloadFromUrlTask.execute(fileUrl);
                getDownloadTaskMap().put(message.getLocalID(), downloadFromUrlTask);

                return;
            }
            else {
                // Save to app's internal file directory
                dir = appContext.getFilesDir();
            }
        }
        else {
            String dirString = TYPE_VIDEO == message.getType() ?
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + TapTalk.getClientAppName(instanceKey) :
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "";

            dir = new File(dirString);
        }
        dir.mkdirs();
        targetFile = new File(dir, filename);
        targetFile = TAPFileUtils.renameDuplicateFile(targetFile);

        DownloadFromUrlTask downloadFromUrlTask = new DownloadFromUrlTask(instanceKey, message, targetFile);
        downloadFromUrlTask.execute(fileUrl);
        getDownloadTaskMap().put(message.getLocalID(), downloadFromUrlTask);
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
        TAPDataManager.getInstance(instanceKey).downloadFile(message.getRoom().getRoomID(), localID, fileID, fileSize, new TAPDefaultDataView<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                new Thread(() -> {
                    try {
                        String mimeType = TAPUtils.getMimeTypeFromMessage(message);
                        //Log.e(TAG, "mimeType: " + mimeType);
                        Bitmap bitmap = getBitmapFromResponse(response, null == mimeType ? IMAGE_JPEG : mimeType);
                        saveImageToCacheAndSendBroadcast(context, localID, fileID, bitmap);
                    } catch (Exception e) {
                        setDownloadFailed(localID, ERROR_CODE_OTHERS, e.getMessage());
                        //Log.e(TAG, "onSuccess exception: ", e);
                    }
                }).start();
            }

            @Override
            public void onError(TAPErrorModel error) {
                setDownloadFailed(localID, error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                setDownloadFailed(localID, ERROR_CODE_OTHERS, errorMessage);
            }

            @Override
            public void endLoading() {
                TAPDataManager.getInstance(instanceKey).removeDownloadSubscriber(localID);
            }
        });
    }

    public void cancelFileDownload(String localID) {
        TAPDataManager.getInstance(instanceKey).cancelFileDownload(localID);
        removeFailedDownload(localID);
        removeDownloadProgressMap(localID);

        if (null != getDownloadTaskMap().get(localID)) {
            getDownloadTaskMap().get(localID).cancel(true);
        }
    }

    private Bitmap getBitmapFromResponse(ResponseBody responseBody, String mimeType) throws Exception {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(mimeType.equals(IMAGE_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, TapTalk.getImageCompressionQuality(instanceKey), out);
        //Log.e(TAG, "getBitmapFromResponse: " + (mimeType.equals(IMAGE_PNG) ? "Bitmap.CompressFormat.PNG" : "Bitmap.CompressFormat.JPEG"));
        out.flush();
        out.close();
        return bitmap;
    }

    // Write file after download with fileID
    private void writeFileToDiskAndSendBroadcast(Context context, TAPMessageModel message, ResponseBody responseBody) {
        if (message.getData() == null) {
            return;
        }
        String localID = message.getLocalID();
        String filename = getFileNameFromMessage(message);

        String folder;
        switch (message.getType()) {
            case TYPE_VIDEO:
                folder = "Videos";
                break;
            case TYPE_VOICE:
                folder = "Voice Notes";
                break;
            default:
                folder = "Files";
                break;
        }

        File dir;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (message.getType() == TYPE_FILE) {
                // Save to Downloads directory if type is file
                String mimeType = TAPUtils.getMimeTypeFromMessage(message);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + TapTalk.getClientAppName(instanceKey));
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

                if (uri == null) {
                    return;
                }

                try {
                    // Write file to disk
                    OutputStream output = contentResolver.openOutputStream(uri);
                    BufferedSink sink = Okio.buffer(Okio.sink(output));
                    sink.writeAll(responseBody.source());
                    sink.close();
                    output.flush();
                    output.close();

                    // Remove message from progress map
                    removeDownloadProgressMap(localID);
                    if (hasFailedDownloads()) {
                        removeFailedDownload(localID);
                    }

                    // Send download success broadcast
                    String path = TAPFileUtils.getFilePath(context, uri);
                    String fileID = (String) message.getData().get(FILE_ID);
                    String fileUrl = (String) message.getData().get(FILE_URL);
                    Intent intent = new Intent(DownloadFinish);
                    intent.putExtra(DownloadLocalID, localID);
                    intent.putExtra(FILE_ID, fileID);
                    intent.putExtra(FILE_URL, fileUrl);
                    if (path != null) {
                        File file = new File(path);
                        Uri fileProviderUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);
                        addFileProviderPath(fileProviderUri, path);
                        intent.putExtra(DownloadedFile, file);
                        intent.putExtra(FILE_URI, fileProviderUri);
                        String key = TAPUtils.getUriKeyFromMessage(message);
                        TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(message.getRoom().getRoomID(), key, fileProviderUri);
                    }
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    setDownloadFailed(localID, ERROR_CODE_OTHERS, e.getMessage());
                }

                return;
            }
            else {
                // Save to app's internal file directory
                dir = appContext.getFilesDir();
            }
        }
        else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + TapTalk.getClientAppName(instanceKey) + "/" + folder);
        }

        dir.mkdirs();

        File noMediaFile = new File(dir, ".nomedia");
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(dir, filename);
        file = TAPFileUtils.renameDuplicateFile(file);
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
            String fileUrl = (String) message.getData().get(FILE_URL);
            addFileProviderPath(fileProviderUri, file.getAbsolutePath());
            scanFile(context, file, TAPUtils.getFileMimeType(file));
            Intent intent = new Intent(DownloadFinish);
            intent.putExtra(DownloadLocalID, localID);
            intent.putExtra(DownloadedFile, file);
            intent.putExtra(FILE_ID, fileID);
            intent.putExtra(FILE_URL, fileUrl);
            intent.putExtra(FILE_URI, fileProviderUri);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            String key = TAPUtils.getUriKeyFromMessage(message);
            TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(message.getRoom().getRoomID(), key, fileProviderUri);
        }
        catch (Exception e) {
            e.printStackTrace();
            setDownloadFailed(localID, ERROR_CODE_OTHERS, e.getMessage());
            Log.e(TAG, "writeFileToDiskAndSendBroadcast: " + e.getMessage());
        }
    }

    // Save image to gallery
    public void writeImageFileToDisk(Context context, Long timestamp, Bitmap bitmap, String mimeType, TapTalkActionInterface listener) {
        new Thread(() -> {
            try {
                String imageFormat = mimeType.equals(IMAGE_PNG) ? ".png" : ".jpeg";
                String filename = TAPTimeFormatter.formatTime(timestamp, "yyyyMMdd_HHmmssSSS") + imageFormat;

                OutputStream out;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + TapTalk.getClientAppName(instanceKey));
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    if (uri == null) {
                        listener.onError("Could not obtain directory.");
                        return;
                    }
                    out = contentResolver.openOutputStream(uri);
                }
                else {
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + TapTalk.getClientAppName(instanceKey));
                    dir.mkdirs();

                    File file = new File(dir, filename);
                    if (file.exists()) {
                        file.delete();
                    }
                    out = new FileOutputStream(file);
                }
                if (out == null) {
                    return;
                }
                bitmap.compress(mimeType.equals(IMAGE_PNG) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, TapTalk.getImageCompressionQuality(instanceKey), out);
                out.flush();
                out.close();
//                scanFile(context, file, TAPUtils.getFileMimeType(file));
                listener.onSuccess(String.format(context.getString(R.string.tap_format_s_successfully_saved), filename));
            }
            catch (Exception e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            }
        }).start();
    }

    // Save video/file to downloads
    public void writeFileToDisk(Context context, TAPMessageModel message, TapTalkActionInterface listener) {
        if (message.getData() == null || getFileMessageUri(message) == null) {
            listener.onError(context.getString(R.string.tap_error_could_not_find_file));
            return;
        }
        new Thread(() -> {
            try {
                String filename = getFileNameFromMessage(message);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String mimeType = TAPUtils.getMimeTypeFromMessage(message);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + TapTalk.getClientAppName(instanceKey));
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

                    if (uri == null) {
                        listener.onError("Could not obtain directory.");
                        return;
                    }

                    Uri sourceUri = getFileMessageUri(message);
                    InputStream input = contentResolver.openInputStream(sourceUri);
                    OutputStream output = contentResolver.openOutputStream(uri);
                    byte[] data = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    input.close();
                    output.flush();
                    output.close();
                    listener.onSuccess(String.format(context.getString(R.string.tap_format_s_successfully_saved), filename));
                }
                else {
                    File dir = new File(TYPE_VIDEO == message.getType() ?
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + TapTalk.getClientAppName(instanceKey) :
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
                    dir.mkdirs();

                    File targetFile = new File(dir, filename);
                    targetFile = TAPFileUtils.renameDuplicateFile(targetFile);

                    File sourceFile = new File(getFileProviderPath(getFileMessageUri(message)));
                    if (sourceFile.exists()) {
                        copyFile(sourceFile, targetFile);
                        scanFile(context, targetFile, TAPUtils.getFileMimeType(targetFile));
                        listener.onSuccess(String.format(context.getString(R.string.tap_format_s_successfully_saved), filename));
                    }
                    else {
                        listener.onError(context.getString(R.string.tap_error_could_not_find_file));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "writeFileToDisk: ", e);
                listener.onError(e.getMessage());
            }
        }).start();
    }

    public void copyFile(File sourceFile, File destFile) {
        try {
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
        catch (IOException e) {
            e.printStackTrace();
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
        HashMap<String, String> filePathMap = TAPDataManager.getInstance(instanceKey).getFileProviderPathMap();
        if (null != filePathMap) {
            getFileProviderPathMap().putAll(filePathMap);
        }
    }

    public void saveFileProviderPathToPreference() {
        if (getFileProviderPathMap().size() > 0) {
            TAPDataManager.getInstance(instanceKey).saveFileProviderPathMap(getFileProviderPathMap());
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
        HashMap<String, HashMap<String, String>> fileUriMap = TAPDataManager.getInstance(instanceKey).getFileMessageUriMap();
        if (null != fileUriMap) {
            // Migration to merge roomID maps to single map
            HashMap<String, HashMap<String, String>> fileUriMapCopy = new HashMap<>(fileUriMap);
            HashMap<String, String> emptyKeyMap = fileUriMapCopy.get("");
            if (emptyKeyMap == null) {
                emptyKeyMap = new HashMap<>();
            }
            for (String key : fileUriMap.keySet()) {
                HashMap<String, String> keyMap = fileUriMap.get(key);
                if (!key.equals("") && keyMap != null) {
                    emptyKeyMap.putAll(keyMap);
                    fileUriMapCopy.remove(key);
                }
            }
            fileUriMapCopy.put("", emptyKeyMap);
            getFileMessageUriMap().putAll(fileUriMapCopy);

            //getFileMessageUriMap().putAll(fileUriMap);
        }
    }

    public void saveFileMessageUriToPreference() {
        TAPDataManager.getInstance(instanceKey).saveFileMessageUriMap(getFileMessageUriMap());
    }

    public Uri getFileMessageUri(String url) {
        return getFileMessageUri("", url);
    }

    public Uri getFileMessageUri(String roomID, String url) {
        if (null == url || url.isEmpty()) {
            return null;
        }
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get("");
        if (null == roomUriMap) {
            roomUriMap = getFileMessageUriMap().get(roomID);
        }
        if (null == roomUriMap) {
            return null;
        }
        String uriString = roomUriMap.get(TAPUtils.getUriKeyFromUrl(url));
        if (uriString == null || uriString.isEmpty()) {
            return null;
        }
        return Uri.parse(uriString);
    }

    public Uri getFileMessageUri(TAPMessageModel message) {
        if (null == message.getData()) {
            return null;
        }
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get("");
        if (null == roomUriMap) {
            roomUriMap = getFileMessageUriMap().get(message.getRoom().getRoomID());
        }
        if (null == roomUriMap) {
            return null;
        }
        String fileUrl = (String) message.getData().get(FILE_URL);
        if (null != fileUrl && !fileUrl.isEmpty() && null != roomUriMap.get(TAPUtils.getUriKeyFromUrl(fileUrl))) {
            return Uri.parse(roomUriMap.get(TAPUtils.getUriKeyFromUrl(fileUrl)));
        } else {
            String fileID = (String) message.getData().get(FILE_ID);
            if (null != fileID && !fileID.isEmpty() && null != roomUriMap.get(fileID)) {
                return Uri.parse(roomUriMap.get(fileID));
            }
        }
        return null;
    }

    public void saveFileMessageUri(String fileID, Uri fileUri) {
        saveFileMessageUri("", fileID, fileUri);
    }

    public void saveFileMessageUri(String roomID, String fileID, Uri fileUri) {
        if (null == fileUri) {
            return;
        }
        saveFileMessageUri(roomID, fileID, fileUri.toString());
    }

    public void saveFileMessageUri(String fileID, String filePath) {
        saveFileMessageUri("", fileID, filePath);
    }

    public void saveFileMessageUri(String roomID, String fileID, String filePath) {
        if (null == fileID || fileID.isEmpty() || null == filePath || filePath.isEmpty()) {
            return;
        }
        //HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get("");
        if (null != roomUriMap) {
            roomUriMap.put(fileID, filePath);
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(fileID, filePath);
            //getFileMessageUriMap().put(roomID, hashMap);
            getFileMessageUriMap().put("", hashMap);
        }
    }

    public void removeFileMessageUri(String roomID, String fileID) {
        HashMap<String, String> roomUriMap = getFileMessageUriMap().get(roomID);
        if (null != roomUriMap) {
            roomUriMap.remove(fileID);
        }
    }

    private HashMap<String, DownloadFromUrlTask> getDownloadTaskMap() {
        return null == downloadTaskMap ? downloadTaskMap = new HashMap<>() : downloadTaskMap;
    }

    public void scanFile(Context context, File f, String mimeType) {
        String path = "";
        if (f != null && !f.getAbsolutePath().isEmpty()) {
            path = f.getAbsolutePath();
        }
        if (mimeType == null) {
            mimeType = "";
        }
        MediaScannerConnection.scanFile(
            context,
            new String[]{path},
            new String[]{mimeType},
            null
        );
    }

    public boolean checkPhysicalFileExists(TAPMessageModel message) {
        if (null == message.getData()) {
            return false;
        }

        return
                (null != getFileMessageUri(message) &&
                        null != getFileProviderPath(getFileMessageUri(message)) &&
                        new File(getFileProviderPath(getFileMessageUri(message))).exists()) ||

                (null != getFileMessageUri(message) &&
                        new File(getFileMessageUri(message).getPath()).exists());
    }

    public void resetTAPFileDownloadManager() {
        getFileProviderPathMap().clear();
        getFileMessageUriMap().clear();
        getDownloadProgressMapBytes().clear();
        getDownloadProgressMapPercent().clear();
        getFailedDownloads().clear();
    }
}

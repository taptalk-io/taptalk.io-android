package io.taptalk.TapTalk.Interface;

import android.graphics.Bitmap;

import java.io.File;

public interface TapTalkDownloadInterface {
    void onDownloadProgress(String localID, long downloaded, long fileSize);
    void onDownloadFinished(String localID);
    void onWriteToStorageFinished(String localID, File file);
}

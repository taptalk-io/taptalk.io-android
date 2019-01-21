package io.taptalk.TapTalk.Interface;

import android.graphics.Bitmap;

import java.io.File;

public interface TapTalkDownloadInterface {
    void onImageDownloadProcessFinished(String localID, Bitmap bitmap);
    void onThumbnailDownloaded(String localID, Bitmap bitmap);
}

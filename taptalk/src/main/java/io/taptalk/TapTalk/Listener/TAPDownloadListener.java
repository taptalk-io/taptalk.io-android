package io.taptalk.TapTalk.Listener;

import android.graphics.Bitmap;

import io.taptalk.TapTalk.Interface.TapTalkDownloadInterface;

public abstract class TAPDownloadListener implements TapTalkDownloadInterface {
    @Override public void onImageDownloadProcessFinished(String localID, Bitmap bitmap) {}
    @Override public void onThumbnailDownloaded(String localID, Bitmap bitmap) {}
}

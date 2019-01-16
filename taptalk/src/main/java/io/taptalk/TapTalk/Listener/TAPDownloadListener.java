package io.taptalk.TapTalk.Listener;

import android.graphics.Bitmap;

import java.io.File;

import io.taptalk.TapTalk.Interface.TapTalkDownloadInterface;

public abstract class TAPDownloadListener implements TapTalkDownloadInterface {
    @Override public void onDownloadProgress(String localID, long downloaded, long fileSize) {}
    @Override public void onDownloadFinished(String localID) {}
    @Override public void onWriteToStorageFinished(String localID, File file, Bitmap bitmap) {}
}

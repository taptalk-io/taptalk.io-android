package io.taptalk.TapTalk.Listener;

import android.graphics.Bitmap;

import java.io.File;

import io.taptalk.TapTalk.Interface.TapTalkDownloadInterface;

public abstract class TAPDownloadListener implements TapTalkDownloadInterface {
    @Override public void onDownloadProcessFinished(String localID, Bitmap bitmap) {}
}

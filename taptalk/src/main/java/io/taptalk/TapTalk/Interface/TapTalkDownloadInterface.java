package io.taptalk.TapTalk.Interface;

import android.graphics.Bitmap;

import java.io.File;

public interface TapTalkDownloadInterface {
    void onDownloadProcessFinished(String localID, Bitmap bitmap);
}

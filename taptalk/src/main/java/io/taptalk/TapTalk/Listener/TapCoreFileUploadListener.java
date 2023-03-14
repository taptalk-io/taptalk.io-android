package io.taptalk.TapTalk.Listener;

import android.net.Uri;

import androidx.annotation.Keep;

import java.io.File;

import io.taptalk.TapTalk.Interface.TapFileUploadInterface;

@Keep
public abstract class TapCoreFileUploadListener implements TapFileUploadInterface {
    @Override
    public void onStart(File file, Uri uri) {

    }

    @Override
    public void onProgress(int percentage, long bytes) {

    }

    @Override
    public void onSuccess(String fileID, String fileURL) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

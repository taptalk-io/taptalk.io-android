package io.taptalk.TapTalk.Interface;

import android.net.Uri;

import java.io.File;

public interface TapFileUploadInterface {
    void onStart(File file, Uri uri);

    void onProgress(int percentage, long bytes);

    void onSuccess(String fileID, String fileURL);

    void onError(String errorCode, String errorMessage);
}

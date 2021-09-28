package io.taptalk.TapTalk.Interface;

import java.io.File;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapFileDownloadInterface {
    void onSuccess(TAPMessageModel message, File file);

    void onError(TAPMessageModel message, String errorCode, String errorMessage);

    void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes);
}


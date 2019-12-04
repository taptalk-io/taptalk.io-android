package io.taptalk.TapTalk.Interface;

import android.net.Uri;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapSendMessageInterface {
    void onStart(TAPMessageModel tapMessageModel);

    void onSuccess(TAPMessageModel tapMessageModel);

    void onError(String errorCode, String errorMessage);

    void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes);

    boolean onRequestFileUpload(TAPMessageModel tapMessageModel, Uri fileUri);
}


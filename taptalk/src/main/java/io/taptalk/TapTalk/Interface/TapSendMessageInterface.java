package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapSendMessageInterface {
    void onStart(TAPMessageModel tapMessageModel);

    void onSuccess(TAPMessageModel tapMessageModel);

    void onError(String errorCode, String errorMessage);

    void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes);
}


package io.taptalk.TapTalk.Interface;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapSendMessageInterface {
    void onStart(TAPMessageModel tapMessageModel);

    void onSuccess(TAPMessageModel tapMessageModel);

    void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage);

    void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes);
}


package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapSendMessageInterface {
    void onStart(TAPMessageModel tapMessageModel);

    void onSuccess(TAPMessageModel tapMessageModel);

    void onError(String errorMessage);

    void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes);
}


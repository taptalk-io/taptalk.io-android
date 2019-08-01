package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapSendChatInterface {
    void onMessageConstructed(TAPMessageModel tapMessageModel);

    void onMessageSent(TAPMessageModel tapMessageModel);

    void onError(String errorMessage);

    void onUploadStarted(String errorMessage);

    void onUploadProgress(String errorMessage);

    void onUploadFinished(String errorMessage);
}


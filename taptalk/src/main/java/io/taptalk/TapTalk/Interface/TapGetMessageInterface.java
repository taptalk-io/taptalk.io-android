package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapGetMessageInterface {
    void onSuccess(List<TAPMessageModel> tapMessageModels);

    void onError(String errorCode, String errorMessage);
}


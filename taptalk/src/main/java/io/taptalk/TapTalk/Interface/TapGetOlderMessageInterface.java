package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapGetOlderMessageInterface {
    void onSuccess(List<TAPMessageModel> messages, Boolean hasMoreData);

    void onError(String errorCode, String errorMessage);
}


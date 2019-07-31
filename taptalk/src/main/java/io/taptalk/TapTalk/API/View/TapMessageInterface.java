package io.taptalk.TapTalk.API.View;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;


public interface TapMessageInterface {
    void onSuccess(List<TAPMessageModel> tapMessageModel);

    void onError(String errorMessage);
}


package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapGetContactInterface {
    void onSuccess(TAPUserModel userModel);

    void onError(String errorCode, String errorMessage);
}

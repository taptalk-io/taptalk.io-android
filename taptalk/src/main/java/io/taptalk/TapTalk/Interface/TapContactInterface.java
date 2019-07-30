package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapContactInterface {
    void onSuccess(TAPUserModel userModel);
    void onError(String errorMessage);
}

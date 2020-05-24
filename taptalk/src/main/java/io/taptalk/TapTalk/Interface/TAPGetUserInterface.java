package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TAPGetUserInterface {
    void getUserSuccess(TAPUserModel userModel);

    void getUserFailed(Throwable throwable);

    void getUserFailed(String errorMessage);
}

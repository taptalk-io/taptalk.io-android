package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapContactListInterface {
    void onSuccess(List<TAPUserModel> userModels);
    void onError(String errorCode, String errorMessage);
}

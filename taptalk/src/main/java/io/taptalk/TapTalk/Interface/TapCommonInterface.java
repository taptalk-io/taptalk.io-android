package io.taptalk.TapTalk.Interface;

public interface TapCommonInterface {
    void onSuccess(String successMessage);

    void onError(String errorCode, String errorMessage);
}

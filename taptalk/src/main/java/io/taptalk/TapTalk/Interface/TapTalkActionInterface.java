package io.taptalk.TapTalk.Interface;

public interface TapTalkActionInterface {
    void onSuccess(String message);
    void onFailure(String errorMessage);
}

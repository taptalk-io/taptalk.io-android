package io.taptalk.TapTalk.Interface;

public interface TAPRequestOTPInterface {
    void onRequestSuccess(long otpID, String otpKey, String phone, boolean succeess, String channel);

    void onRequestFailed(String errorMessage, String errorCode);
}

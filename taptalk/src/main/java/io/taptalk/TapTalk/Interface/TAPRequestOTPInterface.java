package io.taptalk.TapTalk.Interface;

public interface TAPRequestOTPInterface {
    void onRequestSuccess(long otpID, String otpKey, String phone, boolean succeess);

    void onRequestFailed(String errorMessage, String errorCode);
}

package io.taptalk.TapTalk.Interface;

public interface TAPVerifyOTPInterface {
    void verifyOTPSuccessToLogin();
    void verifyOTPSuccessToRegister();
    void verifyOTPFailed(String errorCode, String errorMessage);
}

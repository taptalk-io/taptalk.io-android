package io.taptalk.TapTalk.Interface;

public interface TAPVerifyOTPInterface {
    void verifyOTPSuccessToLogin(String userID, String ticket);
    void verifyOTPSuccessToRegister();
    void verifyOTPFailed(String errorMessage, String errorCode);
}

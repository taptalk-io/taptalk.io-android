package io.taptalk.TapTalk.Interface;

public interface TAPRequestOTPInterface {
    void onRequestSuccess(long otpID, String otpKey, String phone, boolean succeess, String channel, String message, int nextRequestSeconds, String whatsAppFailureReason);

    void onRequestFailed(String errorMessage, String errorCode);
}

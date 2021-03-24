package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPLoginOTPResponse {
    @JsonProperty("success") private boolean success;
    @JsonProperty("message") private String message;
    @JsonProperty("phoneWithCode") private String phoneWithCode;
    @JsonProperty("otpID") private long otpID;
    @JsonProperty("otpKey") private String otpKey;
    @JsonProperty("codeLength") private int codeLength;
    @JsonProperty("channel") private String channel;
    @JsonProperty("whatsAppFailureReason") private String whatsAppFailureReason;
    @JsonProperty("nextRequestSeconds") private int nextRequestSeconds;

    public TAPLoginOTPResponse(boolean success, String message, String phoneWithCode, long otpID, String otpKey, int codeLength, String channel, String whatsAppFailureReason, int nextRequestSeconds) {
        this.success = success;
        this.message = message;
        this.phoneWithCode = phoneWithCode;
        this.otpID = otpID;
        this.otpKey = otpKey;
        this.codeLength = codeLength;
        this.channel = channel;
        this.whatsAppFailureReason = whatsAppFailureReason;
        this.nextRequestSeconds = nextRequestSeconds;
    }

    public TAPLoginOTPResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoneWithCode() {
        return phoneWithCode;
    }

    public void setPhoneWithCode(String phoneWithCode) {
        this.phoneWithCode = phoneWithCode;
    }

    public long getOtpID() {
        return otpID;
    }

    public void setOtpID(long otpID) {
        this.otpID = otpID;
    }

    public String getOtpKey() {
        return otpKey;
    }

    public void setOtpKey(String otpKey) {
        this.otpKey = otpKey;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public void setChannel(String channel) { this.channel = channel; }

    public String getChannel() { return channel; }

    public String getWhatsAppFailureReason() { return whatsAppFailureReason; }

    public void setWhatsAppFailureReason(String whatsAppFailureReason) { this.whatsAppFailureReason = whatsAppFailureReason; }

    public int getNextRequestSeconds() { return nextRequestSeconds; }

    public void setNextRequestSeconds(int nextRequestSeconds) { this.nextRequestSeconds = nextRequestSeconds; }
}

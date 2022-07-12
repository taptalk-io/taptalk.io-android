package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPOTPVerifyRequest {
    @JsonProperty("otpID") private long otpID;
    @JsonProperty("otpKey") private String otpKey;
    @JsonProperty("otpCode") private String otpCode;
    @JsonProperty("reason") private String reason;

    public TAPOTPVerifyRequest(long otpID, String otpKey, String otpCode) {
        this.otpID = otpID;
        this.otpKey = otpKey;
        this.otpCode = otpCode;
    }

    public TAPOTPVerifyRequest(long otpID, String otpKey, String otpCode, String reason) {
        this.otpID = otpID;
        this.otpKey = otpKey;
        this.otpCode = otpCode;
        this.reason = reason;
    }

    public TAPOTPVerifyRequest() {
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

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

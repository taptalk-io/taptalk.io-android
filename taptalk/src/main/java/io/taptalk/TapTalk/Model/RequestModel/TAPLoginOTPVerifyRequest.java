package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPLoginOTPVerifyRequest {
    @JsonProperty("otpID") private long otpID;
    @JsonProperty("otpKey") private String otpKey;
    @JsonProperty("otpCode") private String otpCode;

    public TAPLoginOTPVerifyRequest(long otpID, String otpKey, String otpCode) {
        this.otpID = otpID;
        this.otpKey = otpKey;
        this.otpCode = otpCode;
    }

    public TAPLoginOTPVerifyRequest() {
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
}

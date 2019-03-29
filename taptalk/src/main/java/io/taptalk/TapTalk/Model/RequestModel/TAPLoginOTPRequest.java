package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPLoginOTPRequest {
    @JsonProperty("method") private String method;
    @JsonProperty("countryID") private int countryID;
    @JsonProperty("phone") private String phone;

    public TAPLoginOTPRequest(String method, int countryID, String phone) {
        this.method = method;
        this.countryID = countryID;
        this.phone = phone;
    }

    public TAPLoginOTPRequest() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

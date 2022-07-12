package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPOTPRequest {
    @JsonProperty("method") private String method;
    @JsonProperty("countryID") private int countryID;
    @JsonProperty("phone") private String phone;
    @JsonProperty("channel") private String channel;

    public TAPOTPRequest(String method, int countryID, String phone, String channel) {
        this.method = method;
        this.countryID = countryID;
        this.phone = phone;
        this.channel = channel;
    }

    public TAPOTPRequest(String channel) {
        this.channel = channel;
    }

    public TAPOTPRequest() {
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

    public String getChannel() { return channel; }

    public void setChannel(String channel) { this.channel = channel; }
}

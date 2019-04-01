package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPLoginOTPVerifyResponse {
    @JsonProperty("isRegistered") private boolean isRegistered;
    @JsonProperty("userID") private String userID;
    @JsonProperty("ticket") private String ticket;

    public TAPLoginOTPVerifyResponse(boolean isRegistered, String userID, String ticket) {
        this.isRegistered = isRegistered;
        this.userID = userID;
        this.ticket = ticket;
    }

    public TAPLoginOTPVerifyResponse() {
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}

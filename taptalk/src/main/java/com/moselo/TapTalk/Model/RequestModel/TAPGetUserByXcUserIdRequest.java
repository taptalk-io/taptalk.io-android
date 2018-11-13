package com.moselo.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetUserByXcUserIdRequest {
    @JsonProperty("xcUserID") private String xcUserID;

    public TAPGetUserByXcUserIdRequest(String xcUserID) {
        this.xcUserID = xcUserID;
    }

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
    }
}

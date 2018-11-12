package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpGetUserByXcUserIdRequest {
    @JsonProperty("xcUserID") private String xcUserID;

    public HpGetUserByXcUserIdRequest(String xcUserID) {
        this.xcUserID = xcUserID;
    }

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
    }
}

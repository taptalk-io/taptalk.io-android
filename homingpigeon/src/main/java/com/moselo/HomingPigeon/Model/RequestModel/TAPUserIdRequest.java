package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPUserIdRequest {
    @JsonProperty("userID") private String userID;

    public TAPUserIdRequest(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

package com.moselo.HomingPigeon.Model.RequestModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonRequest {
    @Nullable @JsonProperty("userID") private Integer userID;

    public CommonRequest() {
    }

    public static CommonRequest builderWithUserID (String userID) {
        CommonRequest request = new CommonRequest();
        request.setUserID(Integer.parseInt(userID));
        return request;
    }

    @Nullable
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(@Nullable Integer userID) {
        this.userID = userID;
    }
}

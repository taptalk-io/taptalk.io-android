package com.moselo.HomingPigeon.Model.RequestModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpCommonRequest {
    @Nullable @JsonProperty("userID") private Integer userID;

    public HpCommonRequest() {
    }

    public static HpCommonRequest builderWithUserID (String userID) {
        HpCommonRequest request = new HpCommonRequest();
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

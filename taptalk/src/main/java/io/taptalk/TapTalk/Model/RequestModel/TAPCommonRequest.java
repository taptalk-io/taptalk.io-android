package io.taptalk.TapTalk.Model.RequestModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCommonRequest {
    @Nullable @JsonProperty("userID") private Integer userID;

    public TAPCommonRequest() {
    }

    public static TAPCommonRequest builderWithUserID (String userID) {
        TAPCommonRequest request = new TAPCommonRequest();
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

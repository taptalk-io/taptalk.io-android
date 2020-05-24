package io.taptalk.TapTalk.Model.RequestModel;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCommonRequest {
    @Nullable
    @JsonProperty("userID")
    private Integer userID;
    @Nullable
    @JsonProperty("roomID")
    private String roomID;

    public TAPCommonRequest() {
    }

    public static TAPCommonRequest builderWithUserID(String userID) {
        TAPCommonRequest request = new TAPCommonRequest();
        request.setUserID(Integer.parseInt(userID));
        return request;
    }

    public static TAPCommonRequest builderWithRoomID(String roomID) {
        TAPCommonRequest request = new TAPCommonRequest();
        request.setRoomID(roomID);
        return request;
    }

    @Nullable
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(@Nullable Integer userID) {
        this.userID = userID;
    }

    @Nullable
    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(@Nullable String roomID) {
        this.roomID = roomID;
    }
}

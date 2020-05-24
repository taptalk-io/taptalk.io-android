package io.taptalk.TapTalk.Model.RequestModel;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetRoomByXcRoomIDRequest {
    @Nullable
    @JsonProperty("xcRoomID")
    private String xcRoomID;

    public TAPGetRoomByXcRoomIDRequest(@Nullable String xcRoomID) {
        this.xcRoomID = xcRoomID;
    }

    @Nullable
    public String getXcRoomID() {
        return xcRoomID;
    }

    public void setXcRoomID(@Nullable String xcRoomID) {
        this.xcRoomID = xcRoomID;
    }
}

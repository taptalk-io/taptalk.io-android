package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "roomID",
        "authorization",
})

public class RoomModel {

    @JsonProperty("roomID") private String roomID;
    @Nullable @JsonProperty("authorization") private String authorization;

    public RoomModel(String roomID) {
        this.roomID = roomID;
    }

    public RoomModel() {
    }

    public static RoomModel Builder(String roomID){
        return new RoomModel(roomID);
    }

    @JsonProperty("roomID")
    public String getRoomID() {
        return roomID;
    }

    @JsonProperty("roomID")
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Nullable @JsonProperty("authorization")
    public String getAuthorization() {
        return authorization;
    }

    @JsonProperty("authorization")
    public void setAuthorization(@Nullable String authorization) {
        this.authorization = authorization;
    }
}

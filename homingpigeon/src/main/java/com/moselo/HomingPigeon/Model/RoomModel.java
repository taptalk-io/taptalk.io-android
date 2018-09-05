package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "roomID",
        "roomType",
})

public class RoomModel {

    @JsonProperty("roomID") private String roomID;
    @JsonProperty("type") private int roomType;

    public RoomModel(String roomID, int roomType) {
        this.roomID = roomID;
        this.roomType = roomType;
    }

    public RoomModel() {
    }

    public static RoomModel Builder(String roomID, int roomType){
        return new RoomModel(roomID, roomType);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }
}

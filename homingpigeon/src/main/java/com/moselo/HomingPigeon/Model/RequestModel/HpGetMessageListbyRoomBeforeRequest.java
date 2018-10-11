package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpGetMessageListbyRoomBeforeRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("maxCreated") private Long maxCreated;

    public HpGetMessageListbyRoomBeforeRequest(String roomID, Long maxCreated) {
        this.roomID = roomID;
        this.maxCreated = maxCreated;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Long getMaxCreated() {
        return maxCreated;
    }

    public void setMaxCreated(Long maxCreated) {
        this.maxCreated = maxCreated;
    }
}

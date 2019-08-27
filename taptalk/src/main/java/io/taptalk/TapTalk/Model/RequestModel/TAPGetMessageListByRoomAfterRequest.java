package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetMessageListByRoomAfterRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("minCreated") private Long minCreated;
    @JsonProperty("lastUpdated") private Long lastUpdated;

    public TAPGetMessageListByRoomAfterRequest(String roomID, Long minCreated, Long lastUpdated) {
        this.roomID = roomID;
        this.minCreated = minCreated;
        this.lastUpdated = lastUpdated;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Long getMinCreated() {
        return minCreated;
    }

    public void setMinCreated(Long minCreated) {
        this.minCreated = minCreated;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
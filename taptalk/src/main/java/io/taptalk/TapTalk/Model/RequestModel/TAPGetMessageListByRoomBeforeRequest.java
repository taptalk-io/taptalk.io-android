package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetMessageListByRoomBeforeRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("maxCreated") private Long maxCreated;
    @JsonProperty("limit") private Integer limit;

    public TAPGetMessageListByRoomBeforeRequest(String roomID, Long maxCreated, Integer limit) {
        this.roomID = roomID;
        this.maxCreated = maxCreated;
        this.limit = limit;
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPDeleteRoomRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("checksum") private String checksum;

    public TAPDeleteRoomRequest(String roomID, String checksum) {
        this.roomID = roomID;
        this.checksum = checksum;
    }

    public TAPDeleteRoomRequest() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}

package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TapMessageIdsRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("messageIDs") private List<String> messageIDs;

    public TapMessageIdsRequest(String roomID, List<String> messageIDs) {
        this.roomID = roomID;
        this.messageIDs = messageIDs;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public List<String> getMessageIDs() {
        return messageIDs;
    }

    public void setMessageIDs(List<String> messageIDs) {
        this.messageIDs = messageIDs;
    }
}

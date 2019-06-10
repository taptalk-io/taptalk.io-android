package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPDeleteMessageRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("messageIDs") private List<String> messageIDs;
    @JsonProperty("forEveryone") private boolean isForEveryone;

    public TAPDeleteMessageRequest(String roomID, List<String> messageIDs, boolean isForEveryone) {
        this.roomID = roomID;
        this.messageIDs = messageIDs;
        this.isForEveryone = isForEveryone;
    }

    public TAPDeleteMessageRequest() {
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

    public boolean isForEveryone() {
        return isForEveryone;
    }

    public void setForEveryone(boolean forEveryone) {
        isForEveryone = forEveryone;
    }
}
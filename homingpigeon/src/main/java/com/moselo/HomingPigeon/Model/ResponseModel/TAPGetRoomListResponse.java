package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.TAPMessageModel;

import java.util.List;

public class TAPGetRoomListResponse {
    @JsonProperty("messages") private List<TAPMessageModel> messages;

    public TAPGetRoomListResponse() {
    }

    public TAPGetRoomListResponse(List<TAPMessageModel> messages) {
        this.messages = messages;
    }

    public List<TAPMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<TAPMessageModel> messages) {
        this.messages = messages;
    }
}

package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.MessageModel;

import java.util.List;

public class GetRoomListResponse {
    @JsonProperty("messages") private List<MessageModel> messages;

    public GetRoomListResponse() {
    }

    public GetRoomListResponse(List<MessageModel> messages) {
        this.messages = messages;
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }
}

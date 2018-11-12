package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpMessageModel;

import java.util.List;

public class HpGetRoomListResponse {
    @JsonProperty("messages") private List<HpMessageModel> messages;

    public HpGetRoomListResponse() {
    }

    public HpGetRoomListResponse(List<HpMessageModel> messages) {
        this.messages = messages;
    }

    public List<HpMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<HpMessageModel> messages) {
        this.messages = messages;
    }
}

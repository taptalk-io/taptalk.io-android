package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

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

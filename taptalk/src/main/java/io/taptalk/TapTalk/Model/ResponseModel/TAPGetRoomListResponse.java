package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

public class TAPGetRoomListResponse {
    @JsonProperty("messages") private List<HashMap<String, Object>> messages;

    public TAPGetRoomListResponse() {
    }

    public TAPGetRoomListResponse(List<HashMap<String, Object>> messages) {
        this.messages = messages;
    }

    public List<HashMap<String, Object>> getMessages() {
        return messages;
    }

    public void setMessages(List<HashMap<String, Object>> messages) {
        this.messages = messages;
    }
}

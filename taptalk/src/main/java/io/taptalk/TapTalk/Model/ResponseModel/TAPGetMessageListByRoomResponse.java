package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

public class TAPGetMessageListByRoomResponse {
    @JsonProperty("messages") private List<HashMap<String, Object>> messages;
    @JsonProperty("hasMore") private Boolean hasMore;

    public List<HashMap<String, Object>> getMessages() {
        return messages;
    }

    public void setMessages(List<HashMap<String, Object>> messages) {
        this.messages = messages;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

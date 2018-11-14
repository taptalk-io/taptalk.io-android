package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public class TAPGetMessageListbyRoomResponse {
    @JsonProperty("messages") List<TAPMessageModel> messages;
    @JsonProperty("hasMore") Boolean hasMore;

    public List<TAPMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<TAPMessageModel> messages) {
        this.messages = messages;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

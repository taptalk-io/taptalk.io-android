package com.moselo.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.TAPMessageModel;

import java.util.List;

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

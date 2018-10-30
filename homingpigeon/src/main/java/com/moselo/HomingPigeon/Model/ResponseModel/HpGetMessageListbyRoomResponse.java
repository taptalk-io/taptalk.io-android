package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpMessageModel;

import java.util.List;

public class HpGetMessageListbyRoomResponse {
    @JsonProperty("messages") List<HpMessageModel> messages;
    @JsonProperty("hasMore") Boolean hasMore;

    public List<HpMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<HpMessageModel> messages) {
        this.messages = messages;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

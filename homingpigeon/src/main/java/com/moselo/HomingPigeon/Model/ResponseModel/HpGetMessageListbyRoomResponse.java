package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpMessageListMetadata;
import com.moselo.HomingPigeon.Model.HpMessageModel;

import java.util.List;

public class HpGetMessageListbyRoomResponse {
    @JsonProperty("messages") List<HpMessageModel> messages;
    @JsonProperty("metadata") HpMessageListMetadata metadata;

    public List<HpMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<HpMessageModel> messages) {
        this.messages = messages;
    }

    public HpMessageListMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(HpMessageListMetadata metadata) {
        this.metadata = metadata;
    }
}

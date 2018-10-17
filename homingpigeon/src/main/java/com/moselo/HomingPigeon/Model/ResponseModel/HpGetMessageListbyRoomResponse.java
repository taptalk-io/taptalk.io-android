package com.moselo.HomingPigeon.Model.ResponseModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpMessageListMetadata;
import com.moselo.HomingPigeon.Model.HpMessageModel;

import java.util.List;

public class HpGetMessageListbyRoomResponse {
    @JsonProperty("messages") List<HpMessageModel> messages;
    @Nullable @JsonProperty("metadata") HpMessageListMetadata metadata;

    public List<HpMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<HpMessageModel> messages) {
        this.messages = messages;
    }

    @Nullable
    public HpMessageListMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(@Nullable HpMessageListMetadata metadata) {
        this.metadata = metadata;
    }
}

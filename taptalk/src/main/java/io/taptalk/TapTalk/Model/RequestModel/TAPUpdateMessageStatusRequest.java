package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPUpdateMessageStatusRequest {
    @JsonProperty("messageIDs") private List<String> messageIDs;

    public TAPUpdateMessageStatusRequest(List<String> messageIDs) {
        this.messageIDs = messageIDs;
    }

    public List<String> getMessageIDs() {
        return messageIDs;
    }

    public void setMessageIDs(List<String> messageIDs) {
        this.messageIDs = messageIDs;
    }
}

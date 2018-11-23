package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPUpdateMessageStatusResponse {
    @JsonProperty("updatedMessageIDs") private List<String> updatedMessageIDs;

    public TAPUpdateMessageStatusResponse(List<String> updatedMessageIDs) {
        this.updatedMessageIDs = updatedMessageIDs;
    }

    public TAPUpdateMessageStatusResponse() {
    }

    public List<String> getUpdatedMessageIDs() {
        return updatedMessageIDs;
    }

    public void setUpdatedMessageIDs(List<String> updatedMessageIDs) {
        this.updatedMessageIDs = updatedMessageIDs;
    }
}

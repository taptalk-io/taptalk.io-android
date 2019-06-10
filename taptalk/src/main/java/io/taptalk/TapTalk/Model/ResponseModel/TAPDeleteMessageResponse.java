package io.taptalk.TapTalk.Model.ResponseModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPDeleteMessageResponse {
    @Nullable @JsonProperty("deletedMessageIDs") private List<String> deletedMessageIDs;

    public TAPDeleteMessageResponse(@Nullable List<String> deletedMessageIDs) {
        this.deletedMessageIDs = deletedMessageIDs;
    }

    public TAPDeleteMessageResponse() {
    }

    @Nullable
    public List<String> getDeletedMessageIDs() {
        return deletedMessageIDs;
    }

    public void setDeletedMessageIDs(@Nullable List<String> deletedMessageIDs) {
        this.deletedMessageIDs = deletedMessageIDs;
    }
}

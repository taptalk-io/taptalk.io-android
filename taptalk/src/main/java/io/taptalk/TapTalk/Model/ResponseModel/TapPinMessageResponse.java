package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TapPinMessageResponse {

    @JsonProperty("unpinnedMessageIDs")
    @JsonAlias("pinnedMessageIDs")
    private List<String> messageIDs;

    public void setMessageIDs(List<String> messageIDs) {
        this.messageIDs = messageIDs;
    }

    public List<String> getMessageIDs(){
        return messageIDs;
    }
}

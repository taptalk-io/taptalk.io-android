package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPGetMultipleUserByIdRequest {
    @JsonProperty("ids") private List<String> ids;

    public TAPGetMultipleUserByIdRequest(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}

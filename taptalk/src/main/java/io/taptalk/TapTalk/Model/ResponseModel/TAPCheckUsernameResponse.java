package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPCheckUsernameResponse {
    @JsonProperty("exists") private Boolean exists;

    public TAPCheckUsernameResponse() {}

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
}

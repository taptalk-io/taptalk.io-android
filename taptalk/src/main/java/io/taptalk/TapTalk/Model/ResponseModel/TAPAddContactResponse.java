package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPAddContactResponse {
    @JsonProperty("user") private TAPUserModel user;

    public TAPAddContactResponse() {
    }

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
        this.user = user;
    }
}

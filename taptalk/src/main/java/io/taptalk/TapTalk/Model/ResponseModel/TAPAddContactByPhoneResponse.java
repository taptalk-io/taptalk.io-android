package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPAddContactByPhoneResponse {
    @JsonProperty("users") private List<TAPUserModel> users;

    public TAPAddContactByPhoneResponse() {
    }

    public TAPAddContactByPhoneResponse(List<TAPUserModel> users) {
        this.users = users;
    }

    public List<TAPUserModel> getUsers() {
        return users;
    }

    public void setUsers(List<TAPUserModel> users) {
        this.users = users;
    }
}

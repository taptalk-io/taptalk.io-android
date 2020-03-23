package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPGetMultipleUserResponse {
    @JsonProperty("users") private List<TAPUserModel> users;

    public TAPGetMultipleUserResponse() {
    }

    public List<TAPUserModel> getUsers() {
        return users;
    }

    public void setUsers(List<TAPUserModel> users) {
        this.users = users;
    }
}

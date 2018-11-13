package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetUserByUsernameRequest {
    @JsonProperty("username") private String username;

    public TAPGetUserByUsernameRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

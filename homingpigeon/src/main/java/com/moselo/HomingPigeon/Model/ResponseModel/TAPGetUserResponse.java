package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.TAPUserModel;

public class TAPGetUserResponse {
    @JsonProperty("user") private TAPUserModel user;

    public TAPGetUserResponse() {}

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
        this.user = user;
    }
}

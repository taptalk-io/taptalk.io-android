package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpUserModel;

public class HpGetUserResponse {
    @JsonProperty("user") private HpUserModel user;

    public HpGetUserResponse() {}

    public HpUserModel getUser() {
        return user;
    }

    public void setUser(HpUserModel user) {
        this.user = user;
    }
}

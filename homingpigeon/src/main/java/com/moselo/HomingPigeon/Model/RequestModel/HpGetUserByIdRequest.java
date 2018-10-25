package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpGetUserByIdRequest {
    @JsonProperty("id") private String id;

    public HpGetUserByIdRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

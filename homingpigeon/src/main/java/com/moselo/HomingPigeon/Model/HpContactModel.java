package com.moselo.HomingPigeon.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpContactModel {
    @JsonProperty("user") private HpUserModel user;
    @JsonProperty("isRequestPending") private boolean isRequestPending;
    @JsonProperty("isRequestAccepted") private boolean isRequestAccepted;

    public HpContactModel() {}

    public HpUserModel getUser() {
        return user;
    }

    public void setUser(HpUserModel user) {
        this.user = user;
    }

    public boolean isRequestPending() {
        return isRequestPending;
    }

    public void setRequestPending(boolean requestPending) {
        isRequestPending = requestPending;
    }

    public boolean isRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }
}

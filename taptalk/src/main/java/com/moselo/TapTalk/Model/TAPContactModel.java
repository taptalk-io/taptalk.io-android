package com.moselo.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPContactModel {
    @JsonProperty("user") private TAPUserModel user;
    @JsonProperty("isRequestPending") private boolean isRequestPending;
    @JsonProperty("isRequestAccepted") private boolean isRequestAccepted;

    public TAPContactModel() {}

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
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

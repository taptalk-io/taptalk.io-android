package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPContactModel {
    @JsonProperty("user") private TAPUserModel user;
    @JsonProperty("isRequestPending") private boolean isRequestPending;
    @JsonProperty("isRequestAccepted") private boolean isRequestAccepted;

    public TAPContactModel() {
    }

    public static TAPContactModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPContactModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

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

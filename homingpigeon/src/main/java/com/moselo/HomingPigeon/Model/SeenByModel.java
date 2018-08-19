package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user",
        "userID",
        "at",
})
public class SeenByModel {
    @Nullable @JsonProperty("user") private UserModel user;
    @Nullable @JsonProperty("userID") private String userID;
    @Nullable @JsonProperty("at") private Long at;

    @Nullable @JsonProperty("user")
    public UserModel getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(@Nullable UserModel user) {
        this.user = user;
    }

    @Nullable @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(@Nullable String userID) {
        this.userID = userID;
    }

    @Nullable @JsonProperty("at")
    public Long getAt() {
        return at;
    }

    @JsonProperty("at")
    public void setAt(@Nullable Long at) {
        this.at = at;
    }
}

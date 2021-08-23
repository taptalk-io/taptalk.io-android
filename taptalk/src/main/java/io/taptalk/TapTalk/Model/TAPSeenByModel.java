package io.taptalk.TapTalk.Model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user",
        "userID",
        "at",
})
public class TAPSeenByModel {
    @Nullable
    @JsonProperty("user")
    private TAPUserModel user;
    @Nullable
    @JsonProperty("userID")
    private String userID;
    @Nullable
    @JsonProperty("at")
    private Long at;

    public static TAPSeenByModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPSeenByModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    @Nullable
    @JsonProperty("user")
    public TAPUserModel getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(@Nullable TAPUserModel user) {
        this.user = user;
    }

    @Nullable
    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(@Nullable String userID) {
        this.userID = userID;
    }

    @Nullable
    @JsonProperty("at")
    public Long getAt() {
        return at;
    }

    @JsonProperty("at")
    public void setAt(@Nullable Long at) {
        this.at = at;
    }
}

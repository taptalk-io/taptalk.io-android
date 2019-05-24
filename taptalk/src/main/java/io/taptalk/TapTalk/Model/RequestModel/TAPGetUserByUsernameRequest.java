package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPGetUserByUsernameRequest {
    @JsonProperty("username") private String username;
    @JsonProperty("ignoreCase") private Boolean ignoreCase;

    public TAPGetUserByUsernameRequest(String username, boolean ignoreCase) {
        this.username = username;
        this.ignoreCase = ignoreCase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}

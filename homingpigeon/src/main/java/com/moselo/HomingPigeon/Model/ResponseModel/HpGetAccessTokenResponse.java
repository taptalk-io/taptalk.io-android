package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpUserModel;

public class HpGetAccessTokenResponse {
    @JsonProperty("accessToken") private String accessToken;
    @JsonProperty("accessTokenExpiry") private long accessTokenExpiry;
    @JsonProperty("refreshToken") private String refreshToken;
    @JsonProperty("refreshTokenExpiry") private long refreshTokenExpiry;
    @JsonProperty("user") private HpUserModel user;

    public HpGetAccessTokenResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(long refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public HpUserModel getUser() {
        return user;
    }

    public void setUser(HpUserModel user) {
        this.user = user;
    }
}

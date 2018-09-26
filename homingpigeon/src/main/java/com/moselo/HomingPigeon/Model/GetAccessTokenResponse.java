package com.moselo.HomingPigeon.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAccessTokenResponse {
    @JsonProperty("accessToken") private String accessToken;
    @JsonProperty("accessTokenExpiry") private long accessTokenExpiry;
    @JsonProperty("refreshToken") private String refreshToken;
    @JsonProperty("refreshTokenExpiry") private long refreshTokenExpiry;
    @JsonProperty("user") private UserModel user;

    public GetAccessTokenResponse() {
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}

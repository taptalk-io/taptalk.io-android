package com.moselo.HomingPigeon.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTicketRequest {
    @JsonProperty("userIPAddress") private String userIPAddress;
    @JsonProperty("userAgent") private String userAgent;
    @JsonProperty("userPlatform") private String userPlatform;
    @JsonProperty("userDeviceID") private String userDeviceID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullName") private String fullName;
    @JsonProperty("email") private String email;
    @JsonProperty("phone") private String phone;
    @JsonProperty("username") private String username;

    public AuthTicketRequest(String userIPAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID, String fullName, String email, String phone, String username) {
        this.userIPAddress = userIPAddress;
        this.userAgent = userAgent;
        this.userPlatform = userPlatform;
        this.userDeviceID = userDeviceID;
        this.xcUserID = xcUserID;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
    }

    public AuthTicketRequest() {
    }

    public static AuthTicketRequest toBuilder(String userIPAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID, String fullName, String email, String phone, String username) {
        return new AuthTicketRequest(userIPAddress, userAgent, userPlatform, userDeviceID, xcUserID, fullName, email
        , phone, username);
    }

    public String getUserIPAddress() {
        return userIPAddress;
    }

    public void setUserIPAddress(String userIPAddress) {
        this.userIPAddress = userIPAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserPlatform() {
        return userPlatform;
    }

    public void setUserPlatform(String userPlatform) {
        this.userPlatform = userPlatform;
    }

    public String getUserDeviceID() {
        return userDeviceID;
    }

    public void setUserDeviceID(String userDeviceID) {
        this.userDeviceID = userDeviceID;
    }

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

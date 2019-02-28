package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public class TAPAuthTicketRequest {
    @JsonProperty("userIPAddress") private String userIPAddress;
    @JsonProperty("userAgent") private String userAgent;
    @JsonProperty("userPlatform") private String userPlatform;
    @JsonProperty("userDeviceID") private String userDeviceID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullName") private String fullName;
    @JsonProperty("email") private String email;
    @JsonProperty("phone") private String phone;
    @Nullable
    @JsonProperty("username")
    private String username;
    @Nullable
    @JsonProperty("photoThumbnailURL")
    private String photoThumbnailURL;
    @Nullable
    @JsonProperty("photoFullsizeURL")
    private String photoFullsizeURL;
    @Nullable
    @JsonProperty("userRoleCode")
    private String userRoleCode;

    public TAPAuthTicketRequest(String userIPAddress, String userAgent, String userPlatform,
                                String userDeviceID, String xcUserID, String fullName, String email,
                                String phone, @Nullable String username) {
        this.userIPAddress = userIPAddress;
        this.userAgent = userAgent;
        this.userPlatform = userPlatform;
        this.userDeviceID = userDeviceID;
        this.xcUserID = xcUserID;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        if (39 <= Integer.valueOf(xcUserID) && 43 >= Integer.valueOf(xcUserID)) {
            setPhotoThumbnailURL("");
            setPhotoFullsizeURL("");
        } else {
            setPhotoFullsizeURL("https://s3-ap-southeast-1.amazonaws.com/taptalk-dev/images/" + username + "_1542363733889f.jpg");
            setPhotoThumbnailURL("https://s3-ap-southeast-1.amazonaws.com/taptalk-dev/images/" + username + "_1542363733889t.jpg");
        }
        setUserRoleCode("user");
    }

    public TAPAuthTicketRequest() {
    }

    public static TAPAuthTicketRequest toBuilder(String userIPAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID, String fullName, String email, String phone, String username) {
        return new TAPAuthTicketRequest(userIPAddress, userAgent, userPlatform, userDeviceID, xcUserID, fullName, email
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

    @Nullable
    public String getPhotoThumbnailURL() {
        return photoThumbnailURL;
    }

    public void setPhotoThumbnailURL(@Nullable String photoThumbnailURL) {
        this.photoThumbnailURL = photoThumbnailURL;
    }

    @Nullable
    public String getPhotoFullsizeURL() {
        return photoFullsizeURL;
    }

    public void setPhotoFullsizeURL(@Nullable String photoFullsizeURL) {
        this.photoFullsizeURL = photoFullsizeURL;
    }

    @Nullable
    public String getUserRoleCode() {
        return userRoleCode;
    }

    public void setUserRoleCode(@Nullable String userRoleCode) {
        this.userRoleCode = userRoleCode;
    }
}

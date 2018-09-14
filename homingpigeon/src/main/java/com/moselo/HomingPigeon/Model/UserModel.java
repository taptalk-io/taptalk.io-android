package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "userID",
        "name",
        "username",
        "email",
        "phoneNumber",
        "birthdate",
        "gender",
        "isPermit",
        "isFriend",
        "userRole",
        "lastLogin"
})

public class UserModel {

    @JsonProperty("userID") @JsonAlias("id") private String userID;
    @JsonProperty("fullname") private String name;
    @JsonProperty("imageURL") private ImageURL avatarURL;
    @Nullable @JsonProperty("username") private String username;
    @Nullable @JsonProperty("email") private String email;
    @Nullable @JsonProperty("phone") private String phoneNumber;
    @Nullable @JsonProperty("birthdate") private String birthdate;
    @Nullable @JsonProperty("gender") private String gender;
    @Nullable @JsonProperty("isPermit") private Boolean isPermit;
    @Nullable @JsonProperty("isFriend") private Boolean isFriend;
    @Nullable @JsonProperty("userRole") private String userRole;
    @Nullable @JsonProperty("lastLogin") private Long lastLogin;
    @Nullable @JsonProperty("created") private Long created;
    @Nullable @JsonProperty("updated") private Long updated;

    public UserModel(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public static UserModel Builder(String userID, String name){
        return new UserModel(userID, name);
    }

    public UserModel() {
    }

    @JsonProperty("userID") @JsonAlias("id")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID") @JsonAlias("id")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("fullname")
    public String getName() {
        return name;
    }

    @JsonProperty("fullname")
    public void setName(String name) {
        this.name = name;
    }

    public ImageURL getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(ImageURL avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Nullable @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable @JsonProperty("phone")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone")
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable @JsonProperty("birthdate")
    public String getBirthdate() {
        return birthdate;
    }

    @JsonProperty("birthdate")
    public void setBirthdate(@Nullable String birthdate) {
        this.birthdate = birthdate;
    }

    @Nullable @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(@Nullable String gender) {
        this.gender = gender;
    }

    @Nullable @JsonProperty("isPermit")
    public Boolean getPermit() {
        return isPermit;
    }

    @JsonProperty("isPermit")
    public void setPermit(@Nullable Boolean permit) {
        isPermit = permit;
    }

    @Nullable @JsonProperty("isFriend")
    public Boolean getFriend() {
        return isFriend;
    }

    @JsonProperty("isFriend")
    public void setFriend(@Nullable Boolean friend) {
        isFriend = friend;
    }

    @Nullable @JsonProperty("userRole")
    public String getUserType() {
        return userRole;
    }

    @JsonProperty("userRole")
    public void setUserType(@Nullable String userRole) {
        this.userRole = userRole;
    }

    @Nullable @JsonProperty("lastLogin")
    public Long getLastLogin() {
        return lastLogin;
    }

    @JsonProperty("lastLogin")
    public void setLastLogin(@Nullable Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Nullable
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(@Nullable String userRole) {
        this.userRole = userRole;
    }

    @Nullable
    public Long getCreated() {
        return created;
    }

    public void setCreated(@Nullable Long created) {
        this.created = created;
    }

    @Nullable
    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(@Nullable Long updated) {
        this.updated = updated;
    }
}

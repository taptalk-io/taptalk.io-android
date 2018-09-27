package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserModel implements Parcelable {

    //userID itu userID dari Bisnis Server kalau xcUserID itu userID dari Chat Server
    // (mereka berdua bisa sama bisa juga beda)
    @JsonProperty("userID") @JsonAlias("id") private String userID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullname") private String name;
    @JsonProperty("imageURL") private ImageURL avatarURL;
    @Nullable @JsonProperty("username") private String username;
    @Nullable @JsonProperty("email") private String email;
    @Nullable @JsonProperty("phone") private String phoneNumber;
    @Nullable @JsonProperty("userRole") private UserRoleModel userRole;
    @Nullable @JsonProperty("lastLogin") private Long lastLogin;
    @Nullable @JsonProperty("requireChangePassword") private Boolean requireChangePassword;
    @Nullable @JsonProperty("created") private Long created;
    @Nullable @JsonProperty("updated") private Long updated;
    private boolean isSelected;

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

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
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

    @Nullable @JsonProperty("userRole")
    public UserRoleModel getUserRole() {
        return userRole;
    }

    @JsonProperty("userRole")
    public void setUserRole(@Nullable UserRoleModel userRole) {
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
    public Boolean getRequireChangePassword() {
        return requireChangePassword;
    }

    public void setRequireChangePassword(@Nullable Boolean requireChangePassword) {
        this.requireChangePassword = requireChangePassword;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.xcUserID);
        dest.writeString(this.name);
        dest.writeParcelable(this.avatarURL, flags);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeParcelable(this.userRole, flags);
        dest.writeValue(this.lastLogin);
        dest.writeValue(this.requireChangePassword);
        dest.writeValue(this.created);
        dest.writeValue(this.updated);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected UserModel(Parcel in) {
        this.userID = in.readString();
        this.xcUserID = in.readString();
        this.name = in.readString();
        this.avatarURL = in.readParcelable(ImageURL.class.getClassLoader());
        this.username = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.userRole = in.readParcelable(UserRoleModel.class.getClassLoader());
        this.lastLogin = (Long) in.readValue(Long.class.getClassLoader());
        this.requireChangePassword = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.created = (Long) in.readValue(Long.class.getClassLoader());
        this.updated = (Long) in.readValue(Long.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}

package io.taptalk.TapTalk.Model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(tableName = "MyContact", indices = @Index("isContact"))
public class TAPUserModel implements Parcelable {

    @PrimaryKey @NonNull @JsonProperty("userID") @JsonAlias("id")
    private String userID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullname") private String name;
    @Embedded @JsonProperty("imageURL") private TAPImageURL avatarURL;
    @Nullable @JsonProperty("username") private String username;
    @Nullable @JsonProperty("email") private String email;
    @Nullable @JsonProperty("phone") private String phoneNumber;
    @Nullable @JsonProperty("phoneWithCode") private String phoneWithCode;
    @Embedded @Nullable @JsonProperty("userRole") private TAPUserRoleModel userRole;
    @Nullable @JsonProperty("lastLogin") private Long lastLogin;
    @Nullable @JsonProperty("lastActivity") private Long lastActivity;
    @Nullable @JsonProperty("requireChangePassword") private Boolean requireChangePassword;
    @Nullable @JsonProperty("created") private Long created;
    @Nullable @JsonProperty("updated") private Long updated;
    @Nullable @JsonProperty("deleted") private Long deleted;
    @Nullable @JsonProperty("isRequestPending") private Boolean isRequestPending;
    @Nullable @JsonProperty("isEmailVerified") private Boolean isEmailVerified;
    @Nullable @JsonProperty("isPhoneVerified") private Boolean isPhoneVerified;
    @Nullable @JsonProperty("isRequestAccepted") private Boolean isRequestAccepted;
    @Nullable @JsonProperty("isOnline") private Boolean isOnline;
    @Nullable @JsonProperty("countryID") private Integer countryID;
    @Nullable @JsonProperty("countryCallingCode") private String countryCallingCode;
    @Nullable @JsonIgnore private Integer isContact;
    @Nullable @Ignore @JsonIgnore private boolean isSelected;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TAPUserModel &&
                this.getUserID().equals(((TAPUserModel) obj).getUserID()))
            return true;
        else if (obj instanceof TAPUserModel &&
                !this.getUserID().equals(((TAPUserModel) obj).getUserID()))
            return false;
        else
            return super.equals(obj);
    }

    @Ignore
    public TAPUserModel(String userID, String xcUserID, String name, TAPImageURL avatarURL, @Nullable String username
            , @Nullable String email, @Nullable String phoneNumber, @Nullable TAPUserRoleModel userRole
            , @Nullable Long lastLogin, @Nullable Long lastActivity, @Nullable Boolean requireChangePassword, @Nullable Long created
            , @Nullable Long updated, @Nullable Long deleted) {
        this.userID = userID;
        this.xcUserID = xcUserID;
        this.name = name;
        this.avatarURL = avatarURL;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.lastLogin = lastLogin;
        this.lastActivity = lastActivity;
        this.requireChangePassword = requireChangePassword;
        this.created = created;
        this.updated = updated;
        this.deleted = deleted;
    }

    @Ignore
    public TAPUserModel(@NonNull String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public TAPUserModel() {
    }

    public static TAPUserModel Builder(String userID, String xcUserID, String name, TAPImageURL avatarURL, @Nullable String username
            , @Nullable String email, @Nullable String phoneNumber, @Nullable TAPUserRoleModel userRole
            , @Nullable Long lastLogin, @Nullable Long lastActivity, @Nullable Boolean requireChangePassword, @Nullable Long created
            , @Nullable Long updated, @Nullable Long deleted) {
        return new TAPUserModel(userID, xcUserID, name, avatarURL, username, email, phoneNumber, userRole
                , lastLogin, lastActivity, requireChangePassword, created, updated, deleted);
    }

    public static TAPUserModel Builder(String userID, String name) {
        return new TAPUserModel(userID, name);
    }

    // Previously hpUserModelForAddToDB
    public TAPUserModel setUserAsContact() {
        this.setRequestAccepted(true);
        this.setRequestPending(false);
        this.checkAndSetContact(1);
        return this;
    }

    @JsonProperty("userID")
    @JsonAlias("id")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    @JsonAlias("id")
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

    public TAPImageURL getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(TAPImageURL avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Nullable
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    @JsonProperty("phone")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone")
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    @JsonProperty("userRole")
    public TAPUserRoleModel getUserRole() {
        return userRole;
    }

    @JsonProperty("userRole")
    public void setUserRole(@Nullable TAPUserRoleModel userRole) {
        this.userRole = userRole;
    }

    @Nullable
    @JsonProperty("lastLogin")
    public Long getLastLogin() {
        return lastLogin;
    }

    @JsonProperty("lastLogin")
    public void setLastLogin(@Nullable Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Nullable
    public Long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(@Nullable Long lastActivity) {
        this.lastActivity = lastActivity;
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

    @Nullable
    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(@Nullable Long deleted) {
        this.deleted = deleted;
    }

    @Nullable
    public Boolean getRequestPending() {
        return isRequestPending;
    }

    public void setRequestPending(@Nullable Boolean requestPending) {
        isRequestPending = requestPending;
    }

    @Nullable
    public Boolean getRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(@Nullable Boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }

    @Nullable
    public Integer getIsContact() {
        return isContact;
    }

    public void setIsContact(@Nullable Integer isContact) {
        this.isContact = isContact;
    }

    public void checkAndSetContact(@Nullable Integer isContact) {
        if (null == this.isContact || 0 == this.isContact)
            this.isContact = isContact;
    }

    @Nullable
    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(@Nullable Boolean online) {
        isOnline = online;
    }

    @Nullable
    public String getPhoneWithCode() {
        return phoneWithCode;
    }

    public void setPhoneWithCode(@Nullable String phoneWithCode) {
        this.phoneWithCode = phoneWithCode;
    }

    @Nullable
    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(@Nullable Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    @Nullable
    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(@Nullable Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    @Nullable
    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(@Nullable Integer countryID) {
        this.countryID = countryID;
    }

    @Nullable
    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(@Nullable String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Update when adding fields to model
    public void updateValue(TAPUserModel userModel) {
        this.userID = userModel.getUserID();
        this.xcUserID = userModel.getXcUserID();
        this.name = userModel.getName();
        this.avatarURL = userModel.getAvatarURL();
        this.username = userModel.getUsername();
        this.email = userModel.getEmail();
        this.phoneNumber = userModel.getPhoneNumber();
        this.phoneWithCode = userModel.getPhoneWithCode();
        this.userRole = userModel.getUserRole();
        this.lastLogin = userModel.getLastLogin();
        this.lastActivity = userModel.getLastActivity();
        this.requireChangePassword = userModel.getRequireChangePassword();
        this.created = userModel.getCreated();
        this.updated = userModel.getUpdated();
        this.deleted = userModel.getDeleted();
        this.isRequestPending = userModel.getRequestPending();
        this.isRequestAccepted = userModel.getRequestAccepted();
        this.isEmailVerified = userModel.getEmailVerified();
        this.isPhoneVerified = userModel.getPhoneVerified();
        this.countryID = userModel.getCountryID();
        this.countryCallingCode = userModel.getCountryCallingCode();
        //if (null != this.isContact && this.isContact != 1) {
        if (null == this.isContact || this.isContact != 1) {
            this.isContact = userModel.isContact;
        }
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
        dest.writeString(this.phoneWithCode);
        dest.writeParcelable(this.userRole, flags);
        dest.writeValue(this.lastLogin);
        dest.writeValue(this.lastActivity);
        dest.writeValue(this.requireChangePassword);
        dest.writeValue(this.created);
        dest.writeValue(this.updated);
        dest.writeValue(this.deleted);
        dest.writeValue(this.isRequestPending);
        dest.writeValue(this.isEmailVerified);
        dest.writeValue(this.isPhoneVerified);
        dest.writeValue(this.isRequestAccepted);
        dest.writeValue(this.isOnline);
        dest.writeValue(this.countryID);
        dest.writeString(this.countryCallingCode);
        dest.writeValue(this.isContact);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected TAPUserModel(Parcel in) {
        this.userID = in.readString();
        this.xcUserID = in.readString();
        this.name = in.readString();
        this.avatarURL = in.readParcelable(TAPImageURL.class.getClassLoader());
        this.username = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.phoneWithCode = in.readString();
        this.userRole = in.readParcelable(TAPUserRoleModel.class.getClassLoader());
        this.lastLogin = (Long) in.readValue(Long.class.getClassLoader());
        this.lastActivity = (Long) in.readValue(Long.class.getClassLoader());
        this.requireChangePassword = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.created = (Long) in.readValue(Long.class.getClassLoader());
        this.updated = (Long) in.readValue(Long.class.getClassLoader());
        this.deleted = (Long) in.readValue(Long.class.getClassLoader());
        this.isRequestPending = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isEmailVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isPhoneVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isRequestAccepted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isOnline = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.countryID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.countryCallingCode = in.readString();
        this.isContact = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<TAPUserModel> CREATOR = new Creator<TAPUserModel>() {
        @Override
        public TAPUserModel createFromParcel(Parcel source) {
            return new TAPUserModel(source);
        }

        @Override
        public TAPUserModel[] newArray(int size) {
            return new TAPUserModel[size];
        }
    };
}

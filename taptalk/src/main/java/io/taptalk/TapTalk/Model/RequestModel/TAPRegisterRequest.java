package io.taptalk.TapTalk.Model.RequestModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPRegisterRequest implements Parcelable {
    @JsonProperty("fullname") private String fullName;
    @JsonProperty("username") private String username;
    @JsonProperty("countryID") private Integer countryID;
    @JsonProperty("phone") private String phone;
    @JsonProperty("email") private String email;
    @JsonProperty("password") private String password;

    public TAPRegisterRequest() {
    }

    public TAPRegisterRequest(String fullName, String username, Integer countryID, String phone, String email, String password) {
        this.fullName = fullName;
        this.username = username;
        this.countryID = countryID;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullName);
        dest.writeString(this.username);
        dest.writeValue(this.countryID);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.password);
    }

    protected TAPRegisterRequest(Parcel in) {
        this.fullName = in.readString();
        this.username = in.readString();
        this.countryID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.phone = in.readString();
        this.email = in.readString();
        this.password = in.readString();
    }

    public static final Creator<TAPRegisterRequest> CREATOR = new Creator<TAPRegisterRequest>() {
        @Override
        public TAPRegisterRequest createFromParcel(Parcel source) {
            return new TAPRegisterRequest(source);
        }

        @Override
        public TAPRegisterRequest[] newArray(int size) {
            return new TAPRegisterRequest[size];
        }
    };
}

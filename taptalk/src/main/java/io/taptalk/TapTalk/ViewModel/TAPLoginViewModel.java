package io.taptalk.TapTalk.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TAPLoginViewModel extends AndroidViewModel {

    private long otpID = 0L, lastLoginTimestamp = 0L;
    private String otpKey = "", phoneNumber = "0", phoneNumberWithCode = "0", countryCallingID = "62", channel = "sms";
    private int countryID = 0;

    public TAPLoginViewModel(@NonNull Application application) {
        super(application);
    }

    public long getOtpID() {
        return otpID;
    }

    public void setOtpID(long otpID) {
        this.otpID = otpID;
    }

    public long getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(long lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public String getOtpKey() {
        return otpKey;
    }

    public void setOtpKey(String otpKey) {
        this.otpKey = otpKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberWithCode() {
        return phoneNumberWithCode;
    }

    public void setPhoneNumberWithCode(String phoneNumberWithCode) {
        this.phoneNumberWithCode = phoneNumberWithCode;
    }

    public String getCountryCallingID() {
        return countryCallingID;
    }

    public void setCountryCallingID(String countryCallingID) {
        this.countryCallingID = countryCallingID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getChannel() { return channel; }

    public void setChannel(String channel) { this.channel = channel; }

    public void setLastLoginData(Long otpID, String otpKey, String phoneNumber, String phoneNumberWithCode, int countryID, String countryCallingID, String channel) {
        setOtpID(otpID);
        setOtpKey(otpKey);
        setPhoneNumber(phoneNumber);
        setPhoneNumberWithCode(phoneNumberWithCode);
        setCountryID(countryID);
        setCountryCallingID(countryCallingID);
        setChannel(channel);
    }
}

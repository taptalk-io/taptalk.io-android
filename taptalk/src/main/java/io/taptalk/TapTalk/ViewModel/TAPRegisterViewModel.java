package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

public class TAPRegisterViewModel extends AndroidViewModel {
    private int[] formCheck = {0, 0, 0, 0, 0, 0};
    private int countryID = 0;
    private String countryCallingCode = "62";
    private Uri profilePictureUri;

    public TAPRegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public int[] getFormCheck() {
        return formCheck;
    }

    public void setFormCheck(int[] formCheck) {
        this.formCheck = formCheck;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }
}

package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPRegisterViewModel extends AndroidViewModel {
    private int[] formCheck = {0, 0, 0, 0, 0, 0, 0};
    private int countryID = 0, fontResourceId, textFieldFontColor, textFieldFontColorHint, clickableLabelFontColor;
    private boolean isUpdatingProfile, isUploadingProfilePicture;
    private String countryCallingCode = "62", countryFlagUrl, currentProfilePicture;
    private Uri profilePictureUri;
    private TAPUserModel myUserModel;

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

    public int getFontResourceId() {
        return fontResourceId;
    }

    public void setFontResourceId(int fontResourceId) {
        this.fontResourceId = fontResourceId;
    }

    public int getTextFieldFontColor() {
        return textFieldFontColor;
    }

    public void setTextFieldFontColor(int textFieldFontColor) {
        this.textFieldFontColor = textFieldFontColor;
    }

    public int getTextFieldFontColorHint() {
        return textFieldFontColorHint;
    }

    public void setTextFieldFontColorHint(int textFieldFontColorHint) {
        this.textFieldFontColorHint = textFieldFontColorHint;
    }

    public int getClickableLabelFontColor() {
        return clickableLabelFontColor;
    }

    public void setClickableLabelFontColor(int clickableLabelFontColor) {
        this.clickableLabelFontColor = clickableLabelFontColor;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public String getCountryFlagUrl() {
        return countryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        this.countryFlagUrl = countryFlagUrl;
    }

    public boolean isUpdatingProfile() {
        return isUpdatingProfile;
    }

    public void setUpdatingProfile(boolean updatingProfile) {
        isUpdatingProfile = updatingProfile;
    }

    public boolean isUploadingProfilePicture() {
        return isUploadingProfilePicture;
    }

    public void setUploadingProfilePicture(boolean uploadingProfilePicture) {
        isUploadingProfilePicture = uploadingProfilePicture;
    }

    public String getCurrentProfilePicture() {
        return currentProfilePicture;
    }

    public void setCurrentProfilePicture(String currentProfilePicture) {
        this.currentProfilePicture = currentProfilePicture;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public TAPUserModel getMyUserModel() {
        return null == myUserModel ? TAPChatManager.getInstance().getActiveUser() : myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }
}

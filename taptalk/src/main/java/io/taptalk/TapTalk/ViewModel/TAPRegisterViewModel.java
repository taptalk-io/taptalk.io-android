package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapPhotosItemModel;

public class TAPRegisterViewModel extends AndroidViewModel {
    private String instanceKey = "";
    private int[] formCheck = {0, 0, 0, 0, 0, 0, 0};
    private int countryID = 0, textFieldFontColor, textFieldFontColorHint, clickableLabelFontColor;
    private boolean isUpdatingProfile, isUploadingProfilePicture, isLoadPhotoFailed, isUsernameValid;
    private String countryCallingCode = "62", countryFlagUrl, currentProfilePicture;
    private Uri profilePictureUri;
    private ArrayList<TapPhotosItemModel> profilePictureList;
    private TAPUserModel myUserModel;
    private ViewState state = ViewState.VIEW;

    public enum ViewState {
        VIEW, EDIT
    }

    public static class TAPRegisterViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String instanceKey;

        public TAPRegisterViewModelFactory(Application application, String instanceKey) {
            this.application = application;
            this.instanceKey = instanceKey;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TAPRegisterViewModel(application, instanceKey);
        }
    }

    public TAPRegisterViewModel(@NonNull Application application, String instanceKey) {
        super(application);
        this.instanceKey = instanceKey;
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
        return null == myUserModel ? myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser() : myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public ArrayList<TapPhotosItemModel> getProfilePictureList() {
        return profilePictureList == null ? profilePictureList = new ArrayList<>() : profilePictureList;
    }

    public void setProfilePictureList(ArrayList<TapPhotosItemModel> profilePictureList) {
        this.profilePictureList = profilePictureList;
    }

    public boolean isLoadPhotoFailed() {
        return isLoadPhotoFailed;
    }

    public void setLoadPhotoFailed(boolean loadPhotoFailed) {
        isLoadPhotoFailed = loadPhotoFailed;
    }

    public boolean isUsernameValid() {
        return isUsernameValid;
    }

    public void setUsernameValid(boolean usernameValid) {
        isUsernameValid = usernameValid;
    }

    public ViewState getState() {
        return state;
    }

    public void setState(ViewState state) {
        this.state = state;
    }
}

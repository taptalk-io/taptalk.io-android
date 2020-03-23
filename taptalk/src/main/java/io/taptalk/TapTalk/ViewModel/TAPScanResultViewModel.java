package io.taptalk.TapTalk.ViewModel;

import androidx.lifecycle.ViewModel;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPScanResultViewModel extends ViewModel {

    private TAPUserModel addedContactUserModel;
    private String scanResult;
    private TAPUserModel myUserModel;
    private TAPUserModel contactModel;

    public TAPUserModel getAddedContactUserModel() {
        return addedContactUserModel;
    }

    public void setAddedContactUserModel(TAPUserModel addedContactUserModel) {
        this.addedContactUserModel = addedContactUserModel;
    }

    public String getScanResult() {
        return scanResult;
    }

    public void setScanResult(String scanResult) {
        this.scanResult = scanResult;
    }

    public TAPUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public TAPUserModel getContactModel() {
        return contactModel;
    }

    public void setContactModel(TAPUserModel contactModel) {
        this.contactModel = contactModel;
    }
}

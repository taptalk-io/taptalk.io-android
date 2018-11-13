package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Manager.TAPDataManager;
import com.moselo.HomingPigeon.Model.TAPImageURL;
import com.moselo.HomingPigeon.Model.TAPUserModel;

import java.util.ArrayList;
import java.util.List;

public class TAPContactListViewModel extends AndroidViewModel {

    // Contact List needs to be sorted ascending by name
    private LiveData<List<TAPUserModel>> contactListLive;
    private List<TAPUserModel> contactList;
    private List<TAPUserModel> filteredContacts;
    private List<TAPUserModel> selectedContacts;
    private List<List<TAPUserModel>> separatedContacts;
    private TAPImageURL groupImage;
    private String groupName;
    private boolean isSelecting;

    public TAPContactListViewModel(@NonNull Application application) {
        super(application);
        contactListLive = TAPDataManager.getInstance().getMyContactList();
    }

    public LiveData<List<TAPUserModel>> getContactListLive() {
        return contactListLive;
    }

    public List<TAPUserModel> getContactList() {
        return contactList == null ? contactList = new ArrayList<>() : contactList;
    }

    public void setContactList(List<TAPUserModel> contactList) {
        this.contactList = contactList;
    }

    public List<TAPUserModel> getFilteredContacts() {
        return filteredContacts == null ? filteredContacts = new ArrayList<>() : filteredContacts;
    }

    public void setFilteredContacts(List<TAPUserModel> filteredContacts) {
        this.filteredContacts = filteredContacts;
    }

    public List<TAPUserModel> getSelectedContacts() {
        return selectedContacts == null ? selectedContacts = new ArrayList<>() : selectedContacts;
    }

    public void setSelectedContacts(List<TAPUserModel> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public List<List<TAPUserModel>> getSeparatedContacts() {
        return separatedContacts == null ? separatedContacts = new ArrayList<>() : separatedContacts;
    }

    public void setSeparatedContacts(List<List<TAPUserModel>> separatedContacts) {
        this.separatedContacts = separatedContacts;
    }

    public TAPImageURL getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(TAPImageURL groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }
}

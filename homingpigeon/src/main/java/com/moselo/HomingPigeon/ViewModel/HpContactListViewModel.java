package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpUserModel;

import java.util.ArrayList;
import java.util.List;

public class HpContactListViewModel extends AndroidViewModel {

    // Contact List needs to be sorted ascending by name
    private LiveData<List<HpUserModel>> contactListLive;
    private List<HpUserModel> contactList;
    private List<HpUserModel> filteredContacts;
    private List<HpUserModel> selectedContacts;
    private List<List<HpUserModel>> separatedContacts;
    private String groupImage;
    private String groupName;
    private boolean isSelecting;

    public HpContactListViewModel(@NonNull Application application) {
        super(application);
        contactListLive = HpDataManager.getInstance().getMyContactList();
    }

    public LiveData<List<HpUserModel>> getContactListLive() {
        return contactListLive;
    }

    public List<HpUserModel> getContactList() {
        return contactList == null ? contactList = new ArrayList<>() : contactList;
    }

    public void setContactList(List<HpUserModel> contactList) {
        this.contactList = contactList;
    }

    public List<HpUserModel> getFilteredContacts() {
        return filteredContacts == null ? filteredContacts = new ArrayList<>() : filteredContacts;
    }

    public void setFilteredContacts(List<HpUserModel> filteredContacts) {
        this.filteredContacts = filteredContacts;
    }

    public List<HpUserModel> getSelectedContacts() {
        return selectedContacts == null ? selectedContacts = new ArrayList<>() : selectedContacts;
    }

    public void setSelectedContacts(List<HpUserModel> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public List<List<HpUserModel>> getSeparatedContacts() {
        return separatedContacts == null ? separatedContacts = new ArrayList<>() : separatedContacts;
    }

    public void setSeparatedContacts(List<List<HpUserModel>> separatedContacts) {
        this.separatedContacts = separatedContacts;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
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

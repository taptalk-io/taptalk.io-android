package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPContactListViewModel extends AndroidViewModel {

    // Contact List needs to be sorted ascending by name
    private LiveData<List<TAPUserModel>> contactListLive;
    private List<TAPUserModel> contactList;
    private List<TAPUserModel> filteredContacts;
    private List<TAPUserModel> selectedContacts;
    private List<String> selectedContactsIds;
    private List<List<TAPUserModel>> separatedContacts;
    private TAPImageURL groupImage;
    private String groupName;
    private String roomID;
    private boolean isSelecting;
    private boolean isFirstContactSyncDone;
    private int groupSize = 0;

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

    public void setContactListLive(LiveData<List<TAPUserModel>> contactListLive) {
        this.contactListLive = contactListLive;
    }

    public List<String> getSelectedContactsIds() {
        return null == selectedContactsIds? selectedContactsIds = new ArrayList<>() : selectedContactsIds;
    }

    public void setSelectedContactsIds(List<String> selectedContactsIds) {
        this.selectedContactsIds = selectedContactsIds;
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

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }

    public boolean isFirstContactSyncDone() {
        return isFirstContactSyncDone;
    }

    public void setFirstContactSyncDone(boolean firstContactSyncDone) {
        isFirstContactSyncDone = firstContactSyncDone;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public void addSelectedContact(TAPUserModel contactModel) {
        getSelectedContacts().add(contactModel);
        getSelectedContactsIds().add(contactModel.getUserID());
    }

    public void removeSelectedContact(TAPUserModel contactModel) {
        getSelectedContacts().remove(contactModel);
        getSelectedContactsIds().remove(contactModel.getUserID());
    }
}

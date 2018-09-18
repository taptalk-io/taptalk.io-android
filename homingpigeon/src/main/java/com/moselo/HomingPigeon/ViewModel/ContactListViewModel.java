package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ContactListViewModel extends AndroidViewModel {

    // Contact List needs to be sorted ascending by name
    private List<UserModel> contactList;
    private List<UserModel> filteredContacts;
    private List<UserModel> selectedContacts;
    private List<List<UserModel>> separatedContacts;
    private boolean isSelecting;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
    }

    public List<UserModel> getContactList() {
        return contactList == null ? contactList = new ArrayList<>() : contactList;
    }

    public void setContactList(List<UserModel> contactList) {
        this.contactList = contactList;
    }

    public List<UserModel> getFilteredContacts() {
        return filteredContacts == null ? filteredContacts = new ArrayList<>() : filteredContacts;
    }

    public void setFilteredContacts(List<UserModel> filteredContacts) {
        this.filteredContacts = filteredContacts;
    }

    public List<UserModel> getSelectedContacts() {
        return selectedContacts == null ? selectedContacts = new ArrayList<>() : selectedContacts;
    }

    public void setSelectedContacts(List<UserModel> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public List<List<UserModel>> getSeparatedContacts() {
        return separatedContacts == null ? separatedContacts = new ArrayList<>() : separatedContacts;
    }

    public void setSeparatedContacts(List<List<UserModel>> separatedContacts) {
        this.separatedContacts = separatedContacts;
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }
}

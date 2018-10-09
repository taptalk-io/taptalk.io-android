package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpSearchChatModel;

import java.util.ArrayList;
import java.util.List;

public class HpSearchChatViewModel extends AndroidViewModel {

    private boolean isSearchActive = false;
    private List<HpSearchChatModel> searchList;

    public HpSearchChatViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isSearchActive() {
        return isSearchActive;
    }

    public void setSearchActive(boolean searchActive) {
        isSearchActive = searchActive;
    }

    public List<HpSearchChatModel> getSearchList() {
        return null != searchList? searchList : new ArrayList<>();
    }

    public void setSearchList(List<HpSearchChatModel> searchList) {
        this.searchList = searchList;
    }

    public void addSearchList(HpSearchChatModel model) {
        if (null == searchList)
            searchList = new ArrayList<>();

        searchList.add(model);
    }
}

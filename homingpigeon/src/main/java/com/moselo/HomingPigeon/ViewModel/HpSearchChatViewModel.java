package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;
import com.moselo.HomingPigeon.Model.SearchChatModel;

import java.util.ArrayList;
import java.util.List;

public class HpSearchChatViewModel extends AndroidViewModel {

    private boolean isSearchActive = false;
    private List<SearchChatModel> searchList;

    public HpSearchChatViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isSearchActive() {
        return isSearchActive;
    }

    public void setSearchActive(boolean searchActive) {
        isSearchActive = searchActive;
    }

    public List<SearchChatModel> getSearchList() {
        return null != searchList? searchList : new ArrayList<>();
    }

    public void setSearchList(List<SearchChatModel> searchList) {
        this.searchList = searchList;
    }

    public void addSearchList(SearchChatModel model) {
        if (null == searchList)
            searchList = new ArrayList<>();

        searchList.add(model);
    }
}

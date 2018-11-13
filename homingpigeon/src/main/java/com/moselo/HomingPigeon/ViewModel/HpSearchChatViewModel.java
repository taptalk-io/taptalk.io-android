package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchEntity;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpSearchChatViewModel extends AndroidViewModel {

    private LiveData<List<TAPRecentSearchEntity>> recentSearchList;
    private List<HpSearchChatModel> searchResults;
    private List<HpSearchChatModel> recentSearches;
    private Map<String, HpRoomModel> roomPointer;
    private String searchKeyword;
    private boolean isRecentSearchShown;

    public HpSearchChatViewModel(@NonNull Application application) {
        super(application);
    }

    private Map<String, HpRoomModel> getRoomPointer() {
        return null == roomPointer ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public List<HpSearchChatModel> getSearchResults() {
        return null == searchResults ? searchResults = new ArrayList<>() : searchResults;
    }

    public void setSearchResults(List<HpSearchChatModel> searchResults) {
        this.searchResults = searchResults;
        getRoomPointer().clear();
        for (HpSearchChatModel result : searchResults) {
            HpRoomModel room = result.getRoom();
            if (null != room) {
                getRoomPointer().put(room.getRoomID(), room);
            }
        }
    }

    public void addSearchResult(HpSearchChatModel model) {
        getSearchResults().add(model);
        HpRoomModel room = model.getRoom();
        if (null != room) {
            getRoomPointer().put(room.getRoomID(), room);
        }
    }

    public void clearSearchResults() {
        getSearchResults().clear();
        getRoomPointer().clear();
    }

    public boolean resultContainsRoom(String roomID) {
        return getRoomPointer().containsKey(roomID);
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchList() {
        return null == recentSearchList ? recentSearchList = HpDataManager.getInstance().getRecentSearchLive() : recentSearchList;
    }

    public List<HpSearchChatModel> getRecentSearches() {
        return null == recentSearches ? recentSearches = new ArrayList<>() : recentSearches;
    }

    public void clearRecentSearches() {
        getRecentSearches().clear();
    }

    public void addRecentSearches(HpSearchChatModel item) {
        getRecentSearches().add(item);
    }

    public void setRecentSearches(List<HpSearchChatModel> recentSearches) {
        this.recentSearches = recentSearches;
    }

    public boolean isRecentSearchShown() {
        return isRecentSearchShown;
    }

    public void setRecentSearchShown(boolean recentSearchShown) {
        isRecentSearchShown = recentSearchShown;
    }
}

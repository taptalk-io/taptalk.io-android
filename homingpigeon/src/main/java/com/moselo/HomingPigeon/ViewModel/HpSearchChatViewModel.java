package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpSearchChatViewModel extends AndroidViewModel {

    private List<HpSearchChatModel> searchResults;
    private Map<String, HpRoomModel> roomPointer;
    private String searchKeyword;

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
}

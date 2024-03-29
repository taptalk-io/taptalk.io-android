package io.taptalk.TapTalk.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;

public class TAPSearchChatViewModel extends AndroidViewModel {

    private String instanceKey = "";
    private LiveData<List<TAPRecentSearchEntity>> recentSearchList;
    private List<TAPSearchChatModel> searchResults;
    private List<TAPSearchChatModel> recentSearches;
    private Map<String, TAPRoomModel> roomPointer;
    private ArrayList<TAPMessageModel> selectedMessages;
    private String searchKeyword, pendingSearch;
    private int searchState = 0;

    public final int STATE_RECENT_SEARCHES = 0;
    public final int STATE_IDLE = 1;
    public final int STATE_SEARCHING = 2;
    public final int STATE_PENDING = 3;

    public static class TAPSearchChatViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String instanceKey;

        public TAPSearchChatViewModelFactory(Application application, String instanceKey) {
            this.application = application;
            this.instanceKey = instanceKey;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TAPSearchChatViewModel(application, instanceKey);
        }
    }

    public TAPSearchChatViewModel(@NonNull Application application, String instanceKey) {
        super(application);
        this.instanceKey = instanceKey;
    }

    private Map<String, TAPRoomModel> getRoomPointer() {
        return null == roomPointer ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public List<TAPSearchChatModel> getSearchResults() {
        return null == searchResults ? searchResults = new ArrayList<>() : searchResults;
    }

    public void setSearchResults(List<TAPSearchChatModel> searchResults) {
        this.searchResults = searchResults;
        getRoomPointer().clear();
        for (TAPSearchChatModel result : searchResults) {
            TAPRoomModel room = result.getRoom();
            if (null != room) {
                getRoomPointer().put(room.getRoomID(), room);
            }
        }
    }

    public void addSearchResult(TAPSearchChatModel model) {
        addSearchResult(getSearchResults().size(), model);
    }

    public void addSearchResult(int index, TAPSearchChatModel model) {
        getSearchResults().add(index, model);
        TAPRoomModel room = model.getRoom();
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

    public ArrayList<TAPMessageModel> getSelectedMessages() {
        return selectedMessages;
    }

    public void setSelectedMessages(ArrayList<TAPMessageModel> selectedMessages) {
        this.selectedMessages = selectedMessages;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getPendingSearch() {
        return pendingSearch;
    }

    public void setPendingSearch(String pendingSearch) {
        this.pendingSearch = pendingSearch;
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchList() {
        return null == recentSearchList ? recentSearchList = TAPDataManager.getInstance(instanceKey).getRecentSearchLive() : recentSearchList;
    }

    public List<TAPSearchChatModel> getRecentSearches() {
        return null == recentSearches ? recentSearches = new ArrayList<>() : recentSearches;
    }

    public void clearRecentSearches() {
        getRecentSearches().clear();
    }

    public void addRecentSearches(TAPSearchChatModel item) {
        addRecentSearches(getRecentSearches().size(), item);
    }

    public void addRecentSearches(int index, TAPSearchChatModel item) {
        getRecentSearches().add(index, item);
    }

    public void setRecentSearches(List<TAPSearchChatModel> recentSearches) {
        this.recentSearches = recentSearches;
    }

    public int getSearchState() {
        return searchState;
    }

    public void setSearchState(int searchState) {
        this.searchState = searchState;
    }
}

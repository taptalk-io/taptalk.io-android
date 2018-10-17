package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpSearchChatModel;

import java.util.ArrayList;
import java.util.List;

public class HpSearchChatViewModel extends AndroidViewModel {

    private List<HpSearchChatModel> searchResults;
    private boolean isSearchActive;

    public HpSearchChatViewModel(@NonNull Application application) {
        super(application);
    }

    public List<HpSearchChatModel> getSearchResults() {
        return null != searchResults ? searchResults : new ArrayList<>();
    }

    public void setSearchResults(List<HpSearchChatModel> searchResults) {
        this.searchResults = searchResults;
    }

    public void addSearchResult(HpSearchChatModel model) {
        if (null == searchResults)
            searchResults = new ArrayList<>();

        searchResults.add(model);
    }

    public boolean isSearchActive() {
        return isSearchActive;
    }

    public void setSearchActive(boolean searchActive) {
        isSearchActive = searchActive;
    }
}

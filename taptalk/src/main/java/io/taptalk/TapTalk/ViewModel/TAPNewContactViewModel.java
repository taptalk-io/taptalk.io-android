package io.taptalk.TapTalk.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPNewContactViewModel extends AndroidViewModel {

    private TAPUserModel searchResult;
    private String pendingSearch;

    public TAPNewContactViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPUserModel getSearchResult() {
        return null == searchResult ? searchResult = new TAPUserModel() : searchResult;
    }

    public void setSearchResult(TAPUserModel searchResult) {
        this.searchResult = searchResult;
    }

    public String getPendingSearch() {
        return pendingSearch;
    }

    public void setPendingSearch(String pendingSearch) {
        this.pendingSearch = pendingSearch;
    }
}

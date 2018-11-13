package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.TAPUserModel;

public class HpNewContactViewModel extends AndroidViewModel {

    private TAPUserModel searchResult;
    private String pendingSearch;

    public HpNewContactViewModel(@NonNull Application application) {
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

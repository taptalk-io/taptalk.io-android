package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpUserModel;

public class HpNewContactViewModel extends AndroidViewModel {

    private HpUserModel searchResult;

    public HpNewContactViewModel(@NonNull Application application) {
        super(application);
    }

    public HpUserModel getSearchResult() {
        return null == searchResult ? searchResult = new HpUserModel() : searchResult;
    }

    public void setSearchResult(HpUserModel searchResult) {
        this.searchResult = searchResult;
    }
}

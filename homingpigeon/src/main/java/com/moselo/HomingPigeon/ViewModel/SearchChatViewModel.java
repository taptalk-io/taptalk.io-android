package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class SearchChatViewModel extends AndroidViewModel {

    public boolean isSearchActive = false;

    public SearchChatViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isSearchActive() {
        return isSearchActive;
    }

    public void setSearchActive(boolean searchActive) {
        isSearchActive = searchActive;
    }
}

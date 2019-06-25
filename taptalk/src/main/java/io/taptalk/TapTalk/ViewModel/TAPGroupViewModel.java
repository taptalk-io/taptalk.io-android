package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPGroupViewModel extends AndroidViewModel {

    private TAPRoomModel groupData;
    private String myID;
    private boolean isSearchActive;

    public TAPGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPRoomModel getGroupData() {
        return groupData == null ? groupData = new TAPRoomModel() : groupData;
    }

    public void setGroupData(TAPRoomModel groupData) {
        this.groupData = groupData;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public boolean isSearchActive() {
        return isSearchActive;
    }

    public void setSearchActive(boolean searchActive) {
        isSearchActive = searchActive;
    }
}

package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.TAPRoomModel;

public class HpGroupViewModel extends AndroidViewModel {

    private TAPRoomModel groupData;
    private String myID;

    public HpGroupViewModel(@NonNull Application application) {
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
}

package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpRoomModel;

public class HpGroupViewModel extends AndroidViewModel {

    private HpRoomModel groupData;
    private String myID;

    public HpGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public HpRoomModel getGroupData() {
        return groupData == null ? groupData = new HpRoomModel() : groupData;
    }

    public void setGroupData(HpRoomModel groupData) {
        this.groupData = groupData;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }
}

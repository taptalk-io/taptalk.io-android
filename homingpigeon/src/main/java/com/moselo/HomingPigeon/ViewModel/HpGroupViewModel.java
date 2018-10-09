package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpGroupModel;

public class HpGroupViewModel extends AndroidViewModel {

    private HpGroupModel groupData;
    private String myID;

    public HpGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public HpGroupModel getGroupData() {
        return groupData == null ? groupData = new HpGroupModel() : groupData;
    }

    public void setGroupData(HpGroupModel groupData) {
        this.groupData = groupData;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }
}

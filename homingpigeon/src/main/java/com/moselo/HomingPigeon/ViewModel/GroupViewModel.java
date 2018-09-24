package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.GroupModel;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    private GroupModel groupData;
    private String myID;

    public GroupViewModel(@NonNull Application application) {
        super(application);
    }

    public GroupModel getGroupData() {
        return groupData == null ? groupData = new GroupModel() : groupData;
    }

    public void setGroupData(GroupModel groupData) {
        this.groupData = groupData;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }
}

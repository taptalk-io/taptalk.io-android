package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    private String groupName;
    private Uri groupImage;
    private List<UserModel> groupMembers;

    public GroupViewModel(@NonNull Application application) {
        super(application);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Uri getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(Uri groupImage) {
        this.groupImage = groupImage;
    }

    public List<UserModel> getGroupMembers() {
        return null == groupMembers ? groupMembers = new ArrayList<>() : groupMembers;
    }

    public void setGroupMembers(List<UserModel> groupMembers) {
        this.groupMembers = groupMembers;
    }
}

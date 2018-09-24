package com.moselo.HomingPigeon.Model;

import java.util.List;

public class GroupModel {

    private List<UserModel> groupMembers;
    private String groupName;
    private String groupImage;

    public List<UserModel> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<UserModel> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }
}

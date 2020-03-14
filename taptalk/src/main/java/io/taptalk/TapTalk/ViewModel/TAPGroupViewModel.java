package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPGroupViewModel extends AndroidViewModel {

    private TAPRoomModel groupData;
    private List<TapContactListModel> adapterItems;
    private List<String> participantsIDs;
    private String myID;
    private Uri roomImageUri;
    private int groupAction;
    private boolean isGroupPictureChanged, isGroupPictureStartsEmpty, isGroupNameChanged, isLoading;

    public TAPGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPRoomModel getGroupData() {
        return groupData == null ? groupData = new TAPRoomModel() : groupData;
    }

    public void setGroupData(TAPRoomModel groupData) {
        this.groupData = groupData;
    }

    public List<TapContactListModel> getAdapterItems() {
        return null == adapterItems ? adapterItems = new ArrayList<>() : adapterItems;
    }

    public void setAdapterItems(List<TapContactListModel> adapterItems) {
        this.adapterItems = adapterItems;
    }

    public List<String> getParticipantsIDs() {
        return null == participantsIDs? participantsIDs = new ArrayList<>() : participantsIDs;
    }

    public void setParticipantsIDs(List<String> participantsIDs) {
        this.participantsIDs = participantsIDs;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }


    public Uri getRoomImageUri() {
        return roomImageUri;
    }

    public void setRoomImageUri(Uri roomImageUri) {
        this.roomImageUri = roomImageUri;
    }

    public int getGroupAction() {
        return groupAction;
    }

    public void setGroupAction(int groupAction) {
        this.groupAction = groupAction;
    }

    public boolean isGroupPictureChanged() {
        return isGroupPictureChanged;
    }

    public void setGroupPictureChanged(boolean groupPictureChanged) {
        isGroupPictureChanged = groupPictureChanged;
    }

    public boolean isGroupPictureStartsEmpty() {
        return isGroupPictureStartsEmpty;
    }

    public void setGroupPictureStartsEmpty(boolean groupPictureStartsEmpty) {
        isGroupPictureStartsEmpty = groupPictureStartsEmpty;
    }

    public boolean isGroupNameChanged() {
        return isGroupNameChanged;
    }

    public void setGroupNameChanged(boolean groupNameChanged) {
        isGroupNameChanged = groupNameChanged;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}

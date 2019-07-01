package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPGroupViewModel extends AndroidViewModel {

    private TAPRoomModel groupData;
    private List<String> participantsIDs;
    private String myID;
    private Uri roomImageUri;

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

    public List<String> getParticipantsIDs() {
        return null == participantsIDs? participantsIDs = new ArrayList<>() : participantsIDs;
    }

    public void setParticipantsIDs(List<String> participantsIDs) {
        this.participantsIDs = participantsIDs;
    }

    public Uri getRoomImageUri() {
        return roomImageUri;
    }

    public void setRoomImageUri(Uri roomImageUri) {
        this.roomImageUri = roomImageUri;
    }
}

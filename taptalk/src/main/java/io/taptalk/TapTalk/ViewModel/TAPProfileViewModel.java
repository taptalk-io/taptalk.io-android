package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room;
    private List<TAPMessageModel> sharedMedias;
    private HashMap<String, TAPMessageModel> sharedMediasMap;
    private TAPMessageModel pendingDownloadMessage;

    public TAPProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPRoomModel getRoom() {
        return null == room ? room = new TAPRoomModel() : room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public List<TAPMessageModel> getSharedMedias() {
        return null == sharedMedias ? sharedMedias = new ArrayList<>() : sharedMedias;
    }

    private HashMap<String, TAPMessageModel> getSharedMediasMap() {
        return null == sharedMediasMap ? sharedMediasMap = new HashMap<>() : sharedMediasMap;
    }

    public void addSharedMedia(TAPMessageModel sharedMedia) {
        getSharedMedias().add(sharedMedia);
        getSharedMediasMap().put(sharedMedia.getLocalID(), sharedMedia);
    }

    public TAPMessageModel getSharedMedia(String localID) {
        return getSharedMediasMap().get(localID);
    }

    public TAPMessageModel getPendingDownloadMessage() {
        return pendingDownloadMessage;
    }

    public void setPendingDownloadMessage(TAPMessageModel pendingDownloadMessage) {
        this.pendingDownloadMessage = pendingDownloadMessage;
    }
}

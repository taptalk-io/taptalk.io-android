package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.TAPImageURL;
import com.moselo.HomingPigeon.Model.TAPRoomModel;

import java.util.ArrayList;
import java.util.List;

public class HpProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room;
    private List<TAPImageURL> sharedMedias;

    public HpProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPRoomModel getRoom() {
        return null == room ? room = new TAPRoomModel() : room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public List<TAPImageURL> getSharedMedias() {
        return null == sharedMedias ? sharedMedias = new ArrayList<>() : sharedMedias;
    }

    public void setSharedMedias(List<TAPImageURL> sharedMedias) {
        this.sharedMedias = sharedMedias;
    }
}

package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpRoomModel;

import java.util.ArrayList;
import java.util.List;

public class HpProfileViewModel extends AndroidViewModel {

    private HpRoomModel room;
    private List<HpImageURL> sharedMedias;

    public HpProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public HpRoomModel getRoom() {
        return null == room ? room = new HpRoomModel() : room;
    }

    public void setRoom(HpRoomModel room) {
        this.room = room;
    }

    public List<HpImageURL> getSharedMedias() {
        return null == sharedMedias ? sharedMedias = new ArrayList<>() : sharedMedias;
    }

    public void setSharedMedias(List<HpImageURL> sharedMedias) {
        this.sharedMedias = sharedMedias;
    }
}

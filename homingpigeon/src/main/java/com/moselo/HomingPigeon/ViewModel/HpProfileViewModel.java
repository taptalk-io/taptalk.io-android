package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.HpRoomModel;

public class HpProfileViewModel extends AndroidViewModel {

    private HpRoomModel room;

    public HpProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public HpRoomModel getRoom() {
        return room;
    }

    public void setRoom(HpRoomModel room) {
        this.room = room;
    }
}

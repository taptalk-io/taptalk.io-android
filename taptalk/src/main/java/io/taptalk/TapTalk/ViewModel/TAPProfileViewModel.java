package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room;
    private List<TAPImageURL> sharedMedias;

    public TAPProfileViewModel(@NonNull Application application) {
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

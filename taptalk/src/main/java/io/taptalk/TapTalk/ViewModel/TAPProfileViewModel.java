package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room;
    private List<TAPMessageModel> sharedMedias;

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

    public void setSharedMedias(List<TAPMessageModel> sharedMedias) {
        this.sharedMedias = sharedMedias;
    }
}

package io.taptalk.TapTalk.Listener;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapGetRoomListInterface;
import io.taptalk.TapTalk.Model.TAPRoomListModel;

public abstract class TapCoreGetRoomListListener implements TapGetRoomListInterface {

    @Override
    public void onSuccess(List<TAPRoomListModel> roomLists) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

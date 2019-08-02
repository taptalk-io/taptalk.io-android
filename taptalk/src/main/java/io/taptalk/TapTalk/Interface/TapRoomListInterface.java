package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;


public interface TapRoomListInterface {
    void onSuccess(List<TAPRoomListModel> tapRoomListModel);

    void onError(String errorMessage);
}


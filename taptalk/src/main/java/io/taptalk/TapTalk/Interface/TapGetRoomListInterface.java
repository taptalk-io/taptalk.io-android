package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPRoomListModel;

public interface TapGetRoomListInterface {
    void onSuccess(List<TAPRoomListModel> tapRoomListModel);

    void onError(String errorCode, String errorMessage);
}


package io.taptalk.TapTalk.API.View;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;


public interface TapRoomInterface {
    void onSuccess(TAPRoomModel roomModels);

    void onError(String errorMessage);
}


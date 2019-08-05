package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPRoomModel;

public interface TapCreateGroupWithPictureInterface {
    void onSuccess(TAPRoomModel roomModel, boolean isPictureUploadSuccess);

    void onError(String errorCode, String errorMessage);
}


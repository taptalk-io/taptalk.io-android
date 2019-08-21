package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapCreateGroupWithPictureInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;

public abstract class TapCreateGroupWithPictureListener implements TapCreateGroupWithPictureInterface {

    @Override
    public void onSuccess(TAPRoomModel room, boolean isPictureUploadSuccess) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

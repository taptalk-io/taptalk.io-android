package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapCreateGroupWithPictureInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;

@Keep
public abstract class TapCoreCreateGroupWithPictureListener implements TapCreateGroupWithPictureInterface {

    @Override
    public void onSuccess(TAPRoomModel room, boolean isPictureUploadSuccess) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

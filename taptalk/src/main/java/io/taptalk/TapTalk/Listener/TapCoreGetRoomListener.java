package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapGetRoomInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;

@Keep
public abstract class TapCoreGetRoomListener implements TapGetRoomInterface {

    @Override
    public void onSuccess(TAPRoomModel room) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

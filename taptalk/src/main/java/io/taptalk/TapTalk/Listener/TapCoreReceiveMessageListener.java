package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapReceiveMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreReceiveMessageListener implements TapReceiveMessageInterface {

    @Override
    public void onReceiveMessageInActiveRoom(TAPMessageModel message) {

    }

    @Override
    public void onUpdateMessageInActiveRoom(TAPMessageModel message) {

    }

    @Override
    public void onReceiveMessageInOtherRoom(TAPMessageModel message) {

    }

    @Override
    public void onUpdateMessageInOtherRoom(TAPMessageModel message) {

    }
}

package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapReceiveMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TapReceiveMessageListener implements TapReceiveMessageInterface {

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

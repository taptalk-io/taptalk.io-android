package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapReceiveMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreReceiveMessageListener implements TapReceiveMessageInterface {
    @Override
    public void onReceiveNewMessage(TAPMessageModel message) {

    }

    @Override
    public void onReceiveUpdatedMessage(TAPMessageModel message) {

    }
}

package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkSocketMessageInterface;

@Keep
public abstract class TAPSocketMessageListener implements TapTalkSocketMessageInterface {
    @Override
    public void onReceiveNewEmit(String eventName, String emitData) {
    }
}

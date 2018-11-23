package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkSocketMessageInterface;

public abstract class TAPSocketMessageListener implements TapTalkSocketMessageInterface {
    @Override public void onReceiveNewEmit(String eventName, String emitData) {}
}

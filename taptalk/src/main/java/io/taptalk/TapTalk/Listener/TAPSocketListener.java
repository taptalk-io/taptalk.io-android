package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;

public abstract class TAPSocketListener implements TapTalkSocketInterface {
    @Override public void onReceiveNewEmit(String eventName, String emitData) {}
    @Override public void onSocketConnected() {}
    @Override public void onSocketDisconnected() {}
    @Override public void onSocketConnecting() {}
    @Override public void onSocketError() {}
}

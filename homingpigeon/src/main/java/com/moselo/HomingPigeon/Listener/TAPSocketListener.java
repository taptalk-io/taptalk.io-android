package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.TapTalkSocketInterface;

public abstract class TAPSocketListener implements TapTalkSocketInterface {
    @Override public void onReceiveNewEmit(String eventName, String emitData) {}
    @Override public void onSocketConnected() {}
    @Override public void onSocketDisconnected() {}
    @Override public void onSocketConnecting() {}
    @Override public void onSocketError() {}
}

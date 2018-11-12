package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.HomingPigeonSocketInterface;

public abstract class HpSocketListener implements HomingPigeonSocketInterface {
    @Override public void onReceiveNewEmit(String eventName, String emitData) {}
    @Override public void onSocketConnected() {}
    @Override public void onSocketDisconnected() {}
    @Override public void onSocketConnecting() {}
    @Override public void onSocketError() {}
}

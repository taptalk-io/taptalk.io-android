package com.moselo.HomingPigeon.Listener;

public interface HomingPigeonSocketListener {

    void onReceiveNewEmit(String eventName, String emitData);

    void onSocketConnected();
}

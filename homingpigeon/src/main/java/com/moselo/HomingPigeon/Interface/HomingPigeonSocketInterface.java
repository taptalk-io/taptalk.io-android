package com.moselo.HomingPigeon.Interface;

public interface HomingPigeonSocketInterface {

    void onReceiveNewEmit(String eventName, String emitData);

    void onSocketConnected();

    void onSocketDisconnected();

    void onSocketConnecting();

    void onSocketError();
}

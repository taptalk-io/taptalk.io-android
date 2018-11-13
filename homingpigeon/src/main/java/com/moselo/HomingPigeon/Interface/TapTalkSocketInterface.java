package com.moselo.HomingPigeon.Interface;

public interface TapTalkSocketInterface {

    void onReceiveNewEmit(String eventName, String emitData);

    void onSocketConnected();

    void onSocketDisconnected();

    void onSocketConnecting();

    void onSocketError();
}

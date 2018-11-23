package io.taptalk.TapTalk.Interface;

public interface TapTalkSocketInterface {

    void onSocketConnected();

    void onSocketDisconnected();

    void onSocketConnecting();

    void onSocketError();
}

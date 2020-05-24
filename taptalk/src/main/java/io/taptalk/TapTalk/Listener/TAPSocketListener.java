package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;

@Keep
public abstract class TAPSocketListener implements TapTalkSocketInterface {
    @Override
    public void onSocketConnected() {
    }

    @Override
    public void onSocketDisconnected() {
    }

    @Override
    public void onSocketConnecting() {
    }

    @Override
    public void onSocketError() {
    }
}

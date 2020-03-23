package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkGeneralInterface;

@Keep
public abstract class TAPGeneralListener<T> implements TapTalkGeneralInterface<T> {
    @Override
    public void onClick() {

    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onClick(int position, T item) {

    }
}

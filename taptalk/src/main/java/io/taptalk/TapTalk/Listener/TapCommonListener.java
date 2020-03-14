package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapCommonInterface;

@Keep
public abstract class TapCommonListener implements TapCommonInterface {

    @Override
    public void onSuccess(String successMessage) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

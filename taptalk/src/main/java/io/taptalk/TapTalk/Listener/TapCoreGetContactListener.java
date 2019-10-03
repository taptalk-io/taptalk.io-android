package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapGetContactInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TapCoreGetContactListener implements TapGetContactInterface {

    @Override
    public void onSuccess(TAPUserModel user) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

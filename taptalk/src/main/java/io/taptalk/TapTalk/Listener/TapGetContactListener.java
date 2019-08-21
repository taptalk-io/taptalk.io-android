package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapGetContactInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapGetContactListener implements TapGetContactInterface {

    @Override
    public void onSuccess(TAPUserModel user) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

package io.taptalk.TapTalk.Listener;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapGetMultipleContactInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapCoreGetMultipleContactListener implements TapGetMultipleContactInterface {

    @Override
    public void onSuccess(List<TAPUserModel> users) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

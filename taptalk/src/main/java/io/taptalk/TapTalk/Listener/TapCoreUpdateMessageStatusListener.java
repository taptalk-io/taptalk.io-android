package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapCoreUpdateMessageStatusInterface;

@Keep
public abstract class TapCoreUpdateMessageStatusListener implements TapCoreUpdateMessageStatusInterface {
    @Override
    public void onSuccess(List<String> updatedMessageIDs) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

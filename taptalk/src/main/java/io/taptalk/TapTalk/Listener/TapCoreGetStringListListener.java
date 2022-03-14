package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapCoreGetStringListInterface;

@Keep
public abstract class TapCoreGetStringListListener implements TapCoreGetStringListInterface {
    @Override
    public void onSuccess(List<String> stringList) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

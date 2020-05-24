package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.API.View.TapProjectConfigsInterface;
import io.taptalk.TapTalk.Model.TapConfigs;

@Keep
public abstract class TapCoreProjectConfigsListener implements TapProjectConfigsInterface {

    @Override
    public void onSuccess(TapConfigs configs) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.API.View.TapProjectConfigsInterface;
import io.taptalk.TapTalk.Model.TapConfigs;

public abstract class TapCoreProjectConfigsListener implements TapProjectConfigsInterface {

    @Override
    public void onSuccess(TapConfigs configs) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TapConfigs;

public interface TapProjectConfigsInterface {
    void onSuccess(TapConfigs config);

    void onError(String errorCode, String errorMessage);
}


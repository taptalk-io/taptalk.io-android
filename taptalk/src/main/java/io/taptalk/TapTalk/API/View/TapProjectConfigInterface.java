package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TapConfig;

public interface TapProjectConfigInterface {
    void onSuccess(TapConfig config);

    void onError(String errorCode, String errorMessage);
}


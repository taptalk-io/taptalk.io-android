package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPErrorModel;

public interface TAPLoginInterface {
    void onLoginSuccess();
    void onLoginFailed(TAPErrorModel error);
}

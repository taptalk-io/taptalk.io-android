package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPErrorModel;

public interface TAPSendMessageWithIDListener {
    void sendSuccess();
    void sendFailed(TAPErrorModel errorModel);
}

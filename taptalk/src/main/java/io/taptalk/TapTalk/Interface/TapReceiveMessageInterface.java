package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapReceiveMessageInterface {
    void onReceiveNewMessage(TAPMessageModel message);

    void onReceiveUpdatedMessage(TAPMessageModel message);
}

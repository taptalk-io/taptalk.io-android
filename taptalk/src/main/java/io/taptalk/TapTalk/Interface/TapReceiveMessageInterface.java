package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapReceiveMessageInterface {
    void onReceiveMessageInActiveRoom(TAPMessageModel message);

    void onUpdateMessageInActiveRoom(TAPMessageModel message);

    void onReceiveMessageInOtherRoom(TAPMessageModel message);

    void onUpdateMessageInOtherRoom(TAPMessageModel message);
}

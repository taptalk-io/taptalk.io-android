package io.taptalk.TapTalk.Interface;

import android.net.Uri;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapCoreMessageInterface {
    void onReceiveNewMessage(TAPMessageModel message);

    void onReceiveUpdatedMessage(TAPMessageModel message);

    void onMessageDeleted(TAPMessageModel message);

    void onRequestMessageFileUpload(TAPMessageModel tapMessageModel, Uri fileUri);
}

package io.taptalk.TapTalk.Interface;

import android.net.Uri;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapCoreMessageInterface {
    void onReceiveNewMessage(TAPMessageModel message);

    void onReceiveNewMessage(List<TAPMessageModel> messages);

    void onReceiveUpdatedMessage(TAPMessageModel message);

    void onReceiveUpdatedMessage(List<TAPMessageModel> messages);

    void onMessageDeleted(TAPMessageModel message);

    void onMessageDeleted(List<TAPMessageModel> messages);

    void onRequestMessageFileUpload(TAPMessageModel tapMessageModel, Uri fileUri);
}

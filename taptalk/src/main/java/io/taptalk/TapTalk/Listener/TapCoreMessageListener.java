package io.taptalk.TapTalk.Listener;

import android.net.Uri;

import androidx.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapCoreMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreMessageListener implements TapCoreMessageInterface {
    @Override
    public void onReceiveNewMessage(TAPMessageModel message) {

    }

    @Override
    public void onReceiveNewMessage(List<TAPMessageModel> messages) {

    }

    @Override
    public void onReceiveUpdatedMessage(TAPMessageModel message) {

    }

    @Override
    public void onReceiveUpdatedMessage(List<TAPMessageModel> messages) {

    }

    @Override
    public void onMessageDeleted(TAPMessageModel message) {

    }

    @Override
    public void onMessageDeleted(List<TAPMessageModel> messages) {

    }

    @Override
    public void onRequestMessageFileUpload(TAPMessageModel tapMessageModel, Uri fileUri) {

    }
}

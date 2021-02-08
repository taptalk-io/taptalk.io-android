package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapGetAllMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreGetAllMessageListener implements TapGetAllMessageInterface {

    @Override
    public void onGetLocalMessagesCompleted(List<TAPMessageModel> messages) {

    }

    @Override
    public void onGetAllMessagesCompleted(List<TAPMessageModel> allMessages, List<TAPMessageModel> olderMessages, List<TAPMessageModel> newerMessages) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

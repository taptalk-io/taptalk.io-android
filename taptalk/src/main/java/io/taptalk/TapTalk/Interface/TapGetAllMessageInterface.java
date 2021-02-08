package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapGetAllMessageInterface {
    void onGetLocalMessagesCompleted(List<TAPMessageModel> messages);

    void onGetAllMessagesCompleted(List<TAPMessageModel> allMessages, List<TAPMessageModel> olderMessages, List<TAPMessageModel> newerMessages);

    void onError(String errorCode, String errorMessage);
}

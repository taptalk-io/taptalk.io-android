package io.taptalk.TapTalk.Interface;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapTalkMessageStatusInterface {
    void onReadStatus(List<TAPMessageModel> messageModels);
    void onDeliveredStatus(List<TAPMessageModel> messageModels);
}

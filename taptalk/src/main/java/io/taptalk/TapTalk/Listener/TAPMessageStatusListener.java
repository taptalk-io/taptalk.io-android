package io.taptalk.TapTalk.Listener;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapTalkMessageStatusInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TAPMessageStatusListener implements TapTalkMessageStatusInterface {
    @Override public void onReadStatus(List<TAPMessageModel> messageModels) {}
    @Override public void onDeliveredStatus(List<TAPMessageModel> messageModels) {}
}

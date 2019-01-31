package io.taptalk.TapTalk.View.Adapter;

import android.view.ViewGroup;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPBaseChatViewHolder extends TAPBaseViewHolder<TAPMessageModel> {

    protected TAPBaseChatViewHolder(ViewGroup parent, int itemLayoutId) {
        super(parent, itemLayoutId);
    }

    @Override
    protected void onBind(TAPMessageModel item, int position) {

    }

    protected void setMessage(TAPMessageModel message) {

    }

    protected void receiveSentEvent(TAPMessageModel message) {

    }

    protected void receiveDeliveredEvent(TAPMessageModel message) {

    }

    protected void receiveReadEvent(TAPMessageModel message) {

    }

    protected void markUnreadForMessage(TAPMessageModel item, TAPUserModel myUserModel) {
        if ((null == item.getIsRead() || !item.getIsRead()) && !myUserModel.getUserID().equals(item.getUser().getUserID())
                && (null != item.getSending() && !item.getSending())) {
            item.updateReadMessage();
            new Thread(() -> {
                TAPMessageStatusManager.getInstance().addUnreadListByOne(item.getRoom().getRoomID());
                TAPMessageStatusManager.getInstance().addReadMessageQueue(item.copyMessageModel());
            }).start();
        }
    }
}

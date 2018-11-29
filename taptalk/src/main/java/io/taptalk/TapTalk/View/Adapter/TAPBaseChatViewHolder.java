package io.taptalk.TapTalk.View.Adapter;

import android.view.ViewGroup;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public class TAPBaseChatViewHolder extends TAPBaseViewHolder<TAPMessageModel> {

    protected TAPBaseChatViewHolder(ViewGroup parent, int itemLayoutId) {
        super(parent, itemLayoutId);
    }

    @Override
    protected void onBind(TAPMessageModel item, int position) {

    }

    protected void setMessage(TAPMessageModel message) {

    }

    protected void receiveSentEvent() {

    }

    protected void receiveDeliveredEvent() {

    }

    protected void receiveReadEvent() {

    }
}

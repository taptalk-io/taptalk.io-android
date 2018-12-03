package io.taptalk.TapTalk.Helper;

import android.view.ViewGroup;

import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;

public abstract class TAPBaseCustomBubble<T extends TAPBaseChatViewHolder> {
    private int customBubbleLayoutRes;
    private int messageType;

    public TAPBaseCustomBubble(int customBubbleLayoutRes, int messageType) {
        this.customBubbleLayoutRes = customBubbleLayoutRes;
        this.messageType = messageType;
    }

    public int getCustomBubbleLayoutRes() {
        return customBubbleLayoutRes;
    }

    public void setCustomBubbleLayoutRes(int customBubbleLayoutRes) {
        this.customBubbleLayoutRes = customBubbleLayoutRes;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public abstract T createCustomViewHolder(ViewGroup parent, TAPMessageAdapter adapter, TAPUserModel activeUser);
}

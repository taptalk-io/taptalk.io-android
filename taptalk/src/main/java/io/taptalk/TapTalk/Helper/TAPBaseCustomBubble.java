package io.taptalk.TapTalk.Helper;

import androidx.annotation.Nullable;
import android.view.ViewGroup;

import io.taptalk.TapTalk.Interface.TapTalkBaseCustomInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;

public abstract class TAPBaseCustomBubble<T extends TAPBaseChatViewHolder, E extends TapTalkBaseCustomInterface> {
    private int customBubbleLayoutRes;
    private int messageType;
    private E customBubbleListener;

    public TAPBaseCustomBubble(int customBubbleLayoutRes, int messageType, @Nullable E customBubbleListener) {
        this.customBubbleLayoutRes = customBubbleLayoutRes;
        this.messageType = messageType;
        this.customBubbleListener = customBubbleListener;
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

    public E getCustomBubbleListener() {
        return customBubbleListener;
    }

    public void setCustomBubbleListener(E customBubbleListener) {
        this.customBubbleListener = customBubbleListener;
    }

    public abstract T createCustomViewHolder(ViewGroup parent, TAPMessageAdapter adapter, TAPUserModel activeUser, E customBubbleListener);
}

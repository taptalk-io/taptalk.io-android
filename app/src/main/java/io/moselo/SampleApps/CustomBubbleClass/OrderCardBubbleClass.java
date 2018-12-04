package io.moselo.SampleApps.CustomBubbleClass;

import android.view.ViewGroup;

import io.moselo.SampleApps.CustomBubbleListener.OrderCardBubbleListener;
import io.moselo.SampleApps.CustomBubbleViewHolder.OrderCardVH;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;

public class OrderCardBubbleClass extends TAPBaseCustomBubble<OrderCardVH, OrderCardBubbleListener> {

    public OrderCardBubbleClass(int customBubbleLayoutRes, int messageType, OrderCardBubbleListener listener) {
        super(customBubbleLayoutRes, messageType, listener);
    }

    @Override
    public OrderCardVH createCustomViewHolder(ViewGroup parent, TAPMessageAdapter adapter, TAPUserModel activeUser, OrderCardBubbleListener customBubbleListener) {
        return new OrderCardVH(parent, getCustomBubbleLayoutRes(), adapter, activeUser, customBubbleListener);
    }
}

package io.moselo.SampleApps.CustomBubbleClass;

import android.util.Log;
import android.view.ViewGroup;

import io.moselo.SampleApps.CustomBubbleViewHolder.OrderCardVH;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;

public class OrderCardBubbleClass extends TAPBaseCustomBubble<OrderCardVH> {

    public OrderCardBubbleClass(int customBubbleLayoutRes, int messageType) {
        super(customBubbleLayoutRes, messageType);
    }

    @Override
    public OrderCardVH createCustomViewHolder(ViewGroup parent, TAPMessageAdapter adapter, TAPUserModel activeUser) {
        Log.e("rioriorio", "createCustomViewHolder: " );
        return new OrderCardVH(parent, getCustomBubbleLayoutRes(), adapter, activeUser);
    }
}

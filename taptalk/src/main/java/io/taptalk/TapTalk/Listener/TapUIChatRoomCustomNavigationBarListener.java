package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapUIChatRoomCustomNavigationBarInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;

@Keep
public abstract class TapUIChatRoomCustomNavigationBarListener implements TapUIChatRoomCustomNavigationBarInterface {
    @Override
    public TapBaseChatRoomCustomNavigationBarFragment setCustomChatRoomNavigationBar(Activity activity, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
        return null;
    }

    @Override
    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {

    }
}

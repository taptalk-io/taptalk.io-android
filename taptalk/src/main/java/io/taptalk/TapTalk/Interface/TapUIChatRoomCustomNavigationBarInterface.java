package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;

public interface TapUIChatRoomCustomNavigationBarInterface {

    TapBaseChatRoomCustomNavigationBarFragment setCustomChatRoomNavigationBar(Activity activity, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser);

    void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser);
}

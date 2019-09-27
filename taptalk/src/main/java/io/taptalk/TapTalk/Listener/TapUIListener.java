package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapUIInterface;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapUIListener implements TapUIInterface {
    @Override
    public void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user) {
        TapUI.getInstance().openTapTalkChatProfile(activity, room);
    }

    @Override
    public void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room) {
        TapUI.getInstance().openTapTalkChatProfile(activity, room);
    }

    @Override
    public void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel message, HashMap<String, Object> userInfo) {

    }

    @Override
    public void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }

    @Override
    public void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }
}

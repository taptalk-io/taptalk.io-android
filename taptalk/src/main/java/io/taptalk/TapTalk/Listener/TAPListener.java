package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TAPListener implements TapTalkInterface {
    @Override public void onRefreshAuthTicket() {}

    @Override
    public void onTapTalkChatRoomProfileButtonTapped(Activity activity, TAPUserModel userModel) {
        TapTalk.openTapTalkUserProfile(activity, userModel);
    }

    @Override public void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {}

    @Override
    public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        return null;
    }

    @Override public void onTapTalkProductListBubbleLeftButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room) {}

    @Override public void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room) {}

    @Override public void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo) {}

    @Override public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {}
}

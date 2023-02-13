package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;

public interface TapUIChatRoomInterface {
    void onTapTalkChatRoomOpened(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser);

    void onTapTalkChatRoomClosed(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser);

    void onTapTalkActiveUserSendMessage(Activity activity, TAPMessageModel messageModel, TAPRoomModel room);

    void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user);

    void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room);

    void onTapTalkGroupMemberAvatarTapped(Activity activity, TAPRoomModel room, TAPUserModel user);

    void onTapTalkUserMentionTapped(Activity activity, TAPMessageModel messageModel, TAPUserModel user, boolean isRoomParticipant);

    void onMessageBubbleLongPressed(Activity activity, TAPMessageModel messageModel);

    List<TapLongPressMenuItem> setLongPressMenuItems(TAPMessageModel messageModel);

    void onMessageLongPressMenuItemSelected(Activity activity, TapLongPressMenuItem longPressMenuItem, TAPMessageModel messageModel);

    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, @Nullable HashMap<String, Object> userInfo);

    void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkStarredMessageButtonTapped(Activity activity, TAPRoomModel room);

    void onSavedMessageBubbleArrowTapped(TAPMessageModel message);

    void onPinnedMessageTapped(TAPMessageModel message);

    void onReportMessageButtonTapped(Activity activity, TAPMessageModel message);
}

package io.taptalk.TapTalk.Interface;

import android.app.Activity;
import android.content.Context;

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

    List<TapLongPressMenuItem> setMessageLongPressMenuItems(Context context, TAPMessageModel messageModel);

    List<TapLongPressMenuItem> setScheduledMessageLongPressMenuItems(Context context, TAPMessageModel messageModel);

    List<TapLongPressMenuItem> setLinkLongPressMenuItems(Context context, TAPMessageModel messageModel, String url);

    List<TapLongPressMenuItem> setEmailLongPressMenuItems(Context context, TAPMessageModel messageModel, String emailAddress);

    List<TapLongPressMenuItem> setPhoneLongPressMenuItems(Context context, TAPMessageModel messageModel, String phoneNumber);

    List<TapLongPressMenuItem> setMentionLongPressMenuItems(Context context, TAPMessageModel messageModel, String mentionSpan);

    void onLongPressMenuItemSelected(Activity activity, TapLongPressMenuItem longPressMenuItem, @Nullable TAPMessageModel messageModel);

    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, @Nullable HashMap<String, Object> userInfo);

    void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkStarredMessageButtonTapped(Activity activity, TAPRoomModel room);

    void onSavedMessageBubbleArrowTapped(TAPMessageModel message);

    void onPinnedMessageTapped(TAPMessageModel message);

    void onReportMessageButtonTapped(Activity activity, TAPMessageModel message);
}

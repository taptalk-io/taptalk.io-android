package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Interface.TapUIChatRoomInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TapStarredMessagesActivity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

@Keep
public abstract class TapUIChatRoomListener implements TapUIChatRoomInterface {

    private String instanceKey = "";

    public TapUIChatRoomListener() {
    }

    public TapUIChatRoomListener(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onTapTalkChatRoomOpened(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {

    }

    @Override
    public void onTapTalkChatRoomClosed(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {

    }

    @Override
    public void onTapTalkActiveUserSendMessage(Activity activity, TAPMessageModel messageModel, TAPRoomModel room) {

    }

    @Override
    public void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user) {
        openTapTalkChatProfile(activity, room, null);
    }

    @Override
    public void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room) {
        openTapTalkChatProfile(activity, room, null);
    }

    @Override
    public void onTapTalkGroupMemberAvatarTapped(Activity activity, TAPRoomModel room, TAPUserModel user) {
        openTapTalkChatProfile(activity, room, user);
    }

    @Override
    public void onTapTalkUserMentionTapped(Activity activity, TAPMessageModel messageModel, TAPUserModel user, boolean isRoomParticipant) {
        if (isRoomParticipant) {
            // Open member profile
            TAPChatProfileActivity.Companion.start(activity, instanceKey, messageModel.getRoom(), user);
        } else {
            // Open personal profile
            TAPChatProfileActivity.Companion.start(
                    activity,
                    instanceKey,
                    TAPRoomModel.Builder(
                            TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                                    TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                                    user.getUserID()),
                            user.getFullname(),
                            TYPE_PERSONAL,
                            user.getImageURL(),
                            ""), // TODO: 13 Apr 2020 ROOM COLOR
                    user);
        }
    }

    @Override
    public void onMessageBubbleLongPressed(Activity activity, TAPMessageModel messageModel) {
        setLongPressMenuItems(messageModel);
    }

    @Override
    public List<TapLongPressMenuItem> setLongPressMenuItems(TAPMessageModel messageModel) {
        // TODO:
        return null;
    }

    @Override
    public void onMessageLongPressMenuItemSelected(Activity activity, TapLongPressMenuItem longPressMenuItem, TAPMessageModel messageModel) {

    }

    @Override
    public void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel message, @Nullable HashMap<String, Object> userInfo) {

    }

    @Override
    public void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }

    @Override
    public void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }

    @Override
    public void onTapTalkStarredMessageButtonTapped(Activity activity, TAPRoomModel room) {
        if (null == activity) {
            return;
        }
        TapStarredMessagesActivity.Companion.start(activity, instanceKey, room);
    }

    @Override
    public void onSavedMessageBubbleArrowTapped(TAPMessageModel message) {

    }

    @Override
    public void onPinnedMessageTapped(TAPMessageModel message) {

    }

    @Override
    public void onReportMessageButtonTapped(Activity activity, TAPMessageModel message) {

    }

    private void openTapTalkChatProfile(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
        if (null == activity) {
            return;
        }
        TAPChatProfileActivity.Companion.start(activity, instanceKey, room, user);
    }
}

package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.HashMap;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapUIChatRoomInterface {
    void onTapTalkActiveUserSendMessage(Activity activity, TAPMessageModel messageModel, TAPRoomModel room);

    void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user);

    void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room);

    void onTapTalkGroupMemberAvatarTapped(Activity activity, TAPRoomModel room, TAPUserModel user);

    void onTapTalkUserMentionTapped(Activity activity, TAPMessageModel messageModel, TAPUserModel user, boolean isRoomParticipant);

    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, @Nullable HashMap<String, Object> userInfo);

    void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);
}

package io.taptalk.TapTalk.Interface;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkChatInterface {
    void onReceiveMessageInActiveRoom(TAPMessageModel message);

    void onUpdateMessageInActiveRoom(TAPMessageModel message);

    void onDeleteMessageInActiveRoom(TAPMessageModel message);

    void onReceiveMessageInOtherRoom(TAPMessageModel message);

    void onUpdateMessageInOtherRoom(TAPMessageModel message);

    void onDeleteMessageInOtherRoom(TAPMessageModel message);

    void onSendMessage(TAPMessageModel message);

    void onSendMessagePending(TAPMessageModel message);

    void onReplyMessage(TAPMessageModel message);

    void onRetrySendMessage(TAPMessageModel message);

    void onSendFailed(TAPMessageModel message);

    void onMessageRead(TAPMessageModel message);

    void onLayoutLoaded(TAPMessageModel message);

    void onReceiveStartTyping(TAPTypingModel typingModel);

    void onReceiveStopTyping(TAPTypingModel typingModel);

    void onMessageQuoteClicked(TAPMessageModel message);

    void onMentionClicked(TAPMessageModel message, String username);

    void onGroupMemberAvatarClicked(TAPMessageModel message);

    void onOutsideClicked(TAPMessageModel message);

    void onBubbleExpanded();

    void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser);

    void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus);

    void onReadMessage(String roomID);

    void onMessageSelected(TAPMessageModel message);

    void onArrowButtonClicked(TAPMessageModel message);

    void onRequestUserData(TAPMessageModel message);

    void onChatCleared(TAPRoomModel room);

    void onMuteOrUnmuteRoom(TAPRoomModel room, Long expiredAt);

    void onPinRoom(TAPRoomModel room);

    void onUnpinRoom(TAPRoomModel room);
}

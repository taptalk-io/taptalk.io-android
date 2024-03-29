package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapTalkChatInterface;
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TAPChatListener implements TapTalkChatInterface {
    @Override
    public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
    }

    @Override
    public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
    }

    @Override
    public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
    }

    @Override
    public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
    }

    @Override
    public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
    }

    @Override
    public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
    }

    @Override
    public void onSendMessage(TAPMessageModel message) {
    }

    @Override
    public void onSendMessagePending(TAPMessageModel message) {
    }

    @Override
    public void onReplyMessage(TAPMessageModel message) {
    }

    @Override
    public void onRetrySendMessage(TAPMessageModel message) {
    }

    @Override
    public void onSendFailed(TAPMessageModel message) {
    }

    @Override
    public void onMessageRead(TAPMessageModel message) {
    }

    @Override
    public void onLayoutLoaded(TAPMessageModel message) {
    }

    @Override
    public void onReceiveStartTyping(TAPTypingModel typingModel) {
    }

    @Override
    public void onReceiveStopTyping(TAPTypingModel typingModel) {
    }

    @Override
    public void onMessageQuoteClicked(TAPMessageModel message) {
    }

    @Override
    public void onMentionClicked(TAPMessageModel message, String username) {
    }

    @Override
    public void onGroupMemberAvatarClicked(TAPMessageModel message) {
    }

    @Override
    public void onBubbleTapped(TAPMessageModel message) {
    }

    @Override
    public void onOutsideClicked(TAPMessageModel message) {
    }

    @Override
    public void onBubbleExpanded() {
    }

    @Override
    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {

    }

    @Override
    public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
    }

    @Override
    public void onReadMessage(String roomID) {
    }

    @Override
    public void onMessageSelected(TAPMessageModel message) {

    }

    @Override
    public void onArrowButtonClicked(TAPMessageModel message) {

    }

    @Override
    public void onRequestUserData(TAPMessageModel message) {

    }

    @Override
    public void onChatCleared(TAPRoomModel room) {

    }

    @Override
    public void onMuteOrUnmuteRoom(TAPRoomModel room, Long expiredAt) {

    }

    @Override
    public void onPinRoom(TAPRoomModel room) {

    }

    @Override
    public void onUnpinRoom(TAPRoomModel room) {

    }

    @Override
    public void onMarkRoomAsRead(String roomID) {

    }

    @Override
    public void onMarkRoomAsUnread(String roomID) {

    }

    @Override
    public void onCreateScheduledMessage(TapScheduledMessageModel scheduledMessage) {

    }

    @Override
    public void onScheduledSendFailed(TapScheduledMessageModel scheduledMessage) {

    }

    @Override
    public void onScheduledMessageSent(TapScheduledMessageModel scheduledMessage) {

    }

    @Override
    public void onGetScheduledMessageList() {

    }
}

package io.taptalk.TapTalk.Listener;

import android.graphics.Bitmap;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkAttachmentInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TAPAttachmentListener implements TapTalkAttachmentInterface {

    private String instanceKey = "";

    public TAPAttachmentListener(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onDocumentSelected() {
    }

    @Override
    public void onCameraSelected() {
    }

    @Override
    public void onGallerySelected() {
    }

    @Override
    public void onAudioSelected() {
    }

    @Override
    public void onLocationSelected() {
    }

    @Override
    public void onContactSelected() {
    }

    @Override
    public void onCopySelected(String text) {
    }

    @Override
    public void onReplySelected(TAPMessageModel message) {
    }

    @Override
    public void onForwardSelected(TAPMessageModel message) {
    }

    @Override
    public void onOpenLinkSelected(String url) {
    }

    @Override
    public void onComposeSelected(String emailRecipient) {
    }

    @Override
    public void onPhoneCallSelected(String phoneNumber) {
    }

    @Override
    public void onPhoneSmsSelected(String phoneNumber) {
    }

    @Override
    public void onSaveImageToGallery(TAPMessageModel message) {
    }

    @Override
    public void onSaveVideoToGallery(TAPMessageModel message) {
    }

    @Override
    public void onSaveToDownloads(TAPMessageModel message) {
    }

    @Override
    public void onViewProfileSelected(String username, TAPMessageModel message) {

    }

    @Override
    public void onSendMessageSelected(String username) {

    }

    @Override
    public void onDeleteMessage(String roomID, TAPMessageModel message) {
        TAPDataManager.getInstance(instanceKey).deleteMessagesAPI(roomID, message.getMessageID(), true);
    }

    @Override
    public void setAsMain(int imagePosition) {

    }

    @Override
    public void onImageRemoved(int imagePosition) {

    }

    @Override
    public void onSaveProfilePicture(Bitmap bitmap) {

    }

    @Override
    public void onMessageStarred(TAPMessageModel message) {

    }

    @Override
    public void onEditMessage(TAPMessageModel message) {

    }

    @Override
    public void onMessagePinned(TAPMessageModel message) {

    }

    @Override
    public void onViewInChat(TAPMessageModel message) {

    }

    @Override
    public void onSendNow(TAPMessageModel message) {

    }

    @Override
    public void onRescheduleMessage(TAPMessageModel message) {

    }

    @Override
    public void onReportMessage(TAPMessageModel message) {

    }
}

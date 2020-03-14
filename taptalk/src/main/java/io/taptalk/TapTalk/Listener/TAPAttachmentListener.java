package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkAttachmentInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TAPAttachmentListener implements TapTalkAttachmentInterface {
    @Override public void onDocumentSelected() {}
    @Override public void onCameraSelected() {}
    @Override public void onGallerySelected() {}
    @Override public void onAudioSelected() {}
    @Override public void onLocationSelected() {}
    @Override public void onContactSelected() {}
    @Override public void onCopySelected(String text) {}
    @Override public void onReplySelected(TAPMessageModel message) {}
    @Override public void onForwardSelected(TAPMessageModel message) {}
    @Override public void onOpenLinkSelected(String url) {}
    @Override public void onComposeSelected(String emailRecipient) {}
    @Override public void onPhoneCallSelected(String phoneNumber) {}
    @Override public void onPhoneSmsSelected(String phoneNumber) {}
    @Override public void onSaveImageToGallery(TAPMessageModel message) {}
    @Override public void onSaveVideoToGallery(TAPMessageModel message) {}
    @Override public void onSaveToDownloads(TAPMessageModel message) {}
    @Override public void onDeleteMessage(String roomID, TAPMessageModel message) {
        TAPDataManager.getInstance().deleteMessagesAPI(roomID, message.getMessageID(), true);
    }
}

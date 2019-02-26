package io.taptalk.TapTalk.Interface;

import android.content.Context;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapTalkAttachmentInterface {
    void onDocumentSelected();
    void onCameraSelected();
    void onGallerySelected();
    void onAudioSelected();
    void onLocationSelected();
    void onContactSelected();
    void onCopySelected(String text);
    void onReplySelected(TAPMessageModel message);
    void onForwardSelected(TAPMessageModel message);
    void onOpenLinkSelected(String url);
    void onComposeSelected();
    void onPhoneCallSelected();
    void onPhoneSmsSelected();

}

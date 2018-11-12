package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.AttachmentInterface;
import com.moselo.HomingPigeon.Interface.HomingPigeonChatInterface;
import com.moselo.HomingPigeon.Model.HpMessageModel;

public abstract class HpAttachmentListener implements AttachmentInterface {
    @Override public void onDocumentSelected() {}
    @Override public void onCameraSelected() {}
    @Override public void onGallerySelected() {}
    @Override public void onAudioSelected() {}
    @Override public void onLocationSelected() {}
    @Override public void onContactSelected() {}
}

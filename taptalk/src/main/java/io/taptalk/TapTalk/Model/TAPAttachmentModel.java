package io.taptalk.TapTalk.Model;

import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TAPAttachmentModel {
    private int icon;
    private int titleIds;
    private int id;

    public static final int SELECT_PICTURE_CAMERA = 1;
    public static final int SELECT_PICTURE_GALLERY = 2;

    public static final int ATTACH_DOCUMENT = 11;
    public static final int ATTACH_CAMERA = 12;
    public static final int ATTACH_GALLERY = 13;
    public static final int ATTACH_AUDIO = 14;
    public static final int ATTACH_LOCATION = 15;
    public static final int ATTACH_CONTACT = 16;

    public static final int LONG_PRESS_REPLY = 201;
    public static final int LONG_PRESS_FORWARD = 202;
    public static final int LONG_PRESS_COPY = 203;
    public static final int LONG_PRESS_OPEN_LINK = 204;
    public static final int LONG_PRESS_COMPOSE_EMAIL = 205;
    public static final int LONG_PRESS_CALL = 206;
    public static final int LONG_PRESS_SEND_SMS = 207;
    public static final int LONG_PRESS_SAVE_IMAGE_GALLERY = 208;
    public static final int LONG_PRESS_SAVE_VIDEO_GALLERY = 209;
    public static final int LONG_PRESS_SAVE_DOWNLOADS = 210;
    public static final int LONG_PRESS_DELETE = 211;
    public static final int LONG_PRESS_VIEW_PROFILE = 212;
    public static final int LONG_PRESS_SEND_MESSAGE = 213;

    // Check icon color in Attachment Adapter when adding more IDs

    public TAPAttachmentModel(int icon, int titleIds, int id) {
        this.icon = icon;
        this.titleIds = titleIds;
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitleIds() {
        return titleIds;
    }

    public void setTitleIds(int titleIds) {
        this.titleIds = titleIds;
    }

    public static List<TAPAttachmentModel> createAttachMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        // TODO: 31 January 2019 TEMPORARILY DISABLED AUDIO AND CONTACT FROM ATTACHMENT

        if (!TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled()) {
            // Attach document
            imageResIds.add(R.drawable.tap_ic_documents_white);
            titleResIds.add(R.string.tap_document);
            ids.add(ATTACH_DOCUMENT);
        }

        if (!TapUI.getInstance(instanceKey).isCameraAttachmentDisabled()) {
            // Attach from camera
            imageResIds.add(R.drawable.tap_ic_camera_orange);
            titleResIds.add(R.string.tap_camera);
            ids.add(ATTACH_CAMERA);
        }

        if (!TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled()) {
            // Attach from gallery
            imageResIds.add(R.drawable.tap_ic_gallery_orange);
            titleResIds.add(R.string.tap_gallery);
            ids.add(ATTACH_GALLERY);
        }

//        imageResIds.add(R.drawable.tap_ic_audio_pumpkin_orange);
//        titleResIds.add(R.string.audio);
//        ids.add(ATTACH_AUDIO);

        if (Places.isInitialized() && !TapUI.getInstance(instanceKey).isLocationAttachmentDisabled()) {
            // Attach location
            imageResIds.add(R.drawable.tap_ic_location_orange);
            titleResIds.add(R.string.tap_location);
            ids.add(ATTACH_LOCATION);
        }

//        imageResIds.add(R.drawable.tap_ic_contact_pumpkin_orange);
//        titleResIds.add(R.string.contact);
//        ids.add(ATTACH_CONTACT);

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }

        return attachMenus;
    }

    public static List<TAPAttachmentModel> createImagePickerMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isCameraAttachmentDisabled()) {
            // Take picture from camera
            imageResIds.add(R.drawable.tap_ic_camera_orange);
            titleResIds.add(R.string.tap_camera);
            ids.add(SELECT_PICTURE_CAMERA);
        }

        if (!TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled()) {
            // Pick image from gallery
            imageResIds.add(R.drawable.tap_ic_gallery_orange);
            titleResIds.add(R.string.tap_gallery);
            ids.add(SELECT_PICTURE_GALLERY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createFailedMessageBubbleLongPressMenu() {
        // TODO: 10 April 2019 ADD LONG PRESS MENU FOR FAILED MESSAGES
        return new ArrayList<>();
    }

    public static List<TAPAttachmentModel> createTextBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled()) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled() &&
                messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            // Forward
            imageResIds.add(R.drawable.tap_ic_forward_orange);
            titleResIds.add(R.string.tap_forward);
            ids.add(LONG_PRESS_FORWARD);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled() &&
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red);
            titleResIds.add(R.string.tap_delete);
            ids.add(LONG_PRESS_DELETE);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    public static List<TAPAttachmentModel> createImageBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        HashMap<String, Object> messageData = messageModel.getData();

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled()) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageData) {
            String caption = (String) messageData.get(CAPTION);
            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled() &&
                    null != caption && !caption.isEmpty()) {
                // Copy
                imageResIds.add(R.drawable.tap_ic_copy_orange);
                titleResIds.add(R.string.tap_copy);
                ids.add(LONG_PRESS_COPY);
            }
            String fileID = (String) messageData.get(FILE_ID);
            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled() &&
                    null != fileID &&
                    !fileID.isEmpty() &&
                    (TAPCacheManager.getInstance(TapTalk.appContext).containsCache(fileID) ||
                            null != messageData.get(FILE_URL))) {
                // Save to gallery
                imageResIds.add(R.drawable.tap_ic_download_orange);
                titleResIds.add(R.string.tap_save_to_gallery);
                ids.add(LONG_PRESS_SAVE_IMAGE_GALLERY);
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled() &&
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red);
            titleResIds.add(R.string.tap_delete);
            ids.add(LONG_PRESS_DELETE);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createVideoBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        HashMap<String, Object> messageData = messageModel.getData();

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled()) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageData) {
            String caption = (String) messageData.get(CAPTION);
            if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled() &&
                    null != caption && !caption.isEmpty()) {
                // Copy
                imageResIds.add(R.drawable.tap_ic_copy_orange);
                titleResIds.add(R.string.tap_copy);
                ids.add(LONG_PRESS_COPY);
            }
            if (!TapUI.getInstance(instanceKey).isSaveMediaToGalleryMenuDisabled() &&
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
                // Save to gallery
                imageResIds.add(R.drawable.tap_ic_download_orange);
                titleResIds.add(R.string.tap_save_to_gallery);
                ids.add(LONG_PRESS_SAVE_IMAGE_GALLERY);
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled() &&
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red);
            titleResIds.add(R.string.tap_delete);
            ids.add(LONG_PRESS_DELETE);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    public static List<TAPAttachmentModel> createFileBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        HashMap<String, Object> messageData = messageModel.getData();

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled()) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageData) {
            if (!TapUI.getInstance(instanceKey).isSaveDocumentMenuDisabled() &&
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
                // Save to downloads
                imageResIds.add(R.drawable.tap_ic_download_orange);
                titleResIds.add(R.string.tap_save_to_downloads);
                ids.add(LONG_PRESS_SAVE_DOWNLOADS);
            }
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled() &&
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red);
            titleResIds.add(R.string.tap_delete);
            ids.add(LONG_PRESS_DELETE);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createLocationBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isReplyMessageMenuDisabled()) {
            // Reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (!TapUI.getInstance(instanceKey).isForwardMessageMenuDisabled() &&
                messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            // Forward
            imageResIds.add(R.drawable.tap_ic_forward_orange);
            titleResIds.add(R.string.tap_forward);
            ids.add(LONG_PRESS_FORWARD);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        if (!TapUI.getInstance(instanceKey).isDeleteMessageMenuDisabled() &&
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            // Delete
            imageResIds.add(R.drawable.tap_ic_delete_red);
            titleResIds.add(R.string.tap_delete);
            ids.add(LONG_PRESS_DELETE);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createLinkLongPressMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isOpenLinkMenuDisabled()) {
            // Open link
            imageResIds.add(R.drawable.tap_ic_open_link_orange);
            titleResIds.add(R.string.tap_open);
            ids.add(LONG_PRESS_OPEN_LINK);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createEmailLongPressMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isComposeEmailMenuDisabled()) {
            // Compose email
            imageResIds.add(R.drawable.tap_ic_mail_orange);
            titleResIds.add(R.string.tap_compose);
            ids.add(LONG_PRESS_COMPOSE_EMAIL);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createPhoneLongPressMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isDialNumberMenuDisabled()) {
            // Dial number
            imageResIds.add(R.drawable.tap_ic_call_orange);
            titleResIds.add(R.string.tap_call);
            ids.add(LONG_PRESS_CALL);
        }

        if (!TapUI.getInstance(instanceKey).isSendSMSMenuDisabled()) {
            // Send SMS
            imageResIds.add(R.drawable.tap_ic_sms_orange);
            titleResIds.add(R.string.tap_send_sms);
            ids.add(LONG_PRESS_SEND_SMS);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createMentionLongPressMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isViewProfileMenuDisabled()) {
            // View profile
            imageResIds.add(R.drawable.tap_ic_contact_orange);
            titleResIds.add(R.string.tap_view_profile);
            ids.add(LONG_PRESS_VIEW_PROFILE);
        }

        if (!TapUI.getInstance(instanceKey).isSendMessageMenuDisabled()) {
            // Send message
            imageResIds.add(R.drawable.tap_ic_sms_orange);
            titleResIds.add(R.string.tap_send_message);
            ids.add(LONG_PRESS_SEND_MESSAGE);
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            titleResIds.add(R.string.tap_copy);
            ids.add(LONG_PRESS_COPY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createCopyLongPressMenu(String instanceKey) {
        List<TAPAttachmentModel> attachMenus = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled()) {
            // Copy
            attachMenus.add(new TAPAttachmentModel(
                    R.drawable.tap_ic_copy_orange,
                    R.string.tap_copy,
                    LONG_PRESS_COPY));
        }

        return attachMenus;
    }
}

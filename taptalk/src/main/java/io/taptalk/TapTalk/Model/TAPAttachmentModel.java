package io.taptalk.TapTalk.Model;

import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
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

    public static List<TAPAttachmentModel> createAttachMenu() {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        // TODO: 31 January 2019 TEMPORARILY DISABLED AUDIO AND CONTACT FROM ATTACHMENT

        imageResIds.add(R.drawable.tap_ic_documents_white);
        titleResIds.add(R.string.tap_document);
        ids.add(ATTACH_DOCUMENT);

        imageResIds.add(R.drawable.tap_ic_camera_pumpkin_orange);
        titleResIds.add(R.string.tap_camera);
        ids.add(ATTACH_CAMERA);

        imageResIds.add(R.drawable.tap_ic_gallery_pumpkin_orange);
        titleResIds.add(R.string.tap_gallery);
        ids.add(ATTACH_GALLERY);

//        imageResIds.add(R.drawable.tap_ic_audio_pumpkin_orange);
//        titleResIds.add(R.string.audio);
//        ids.add(ATTACH_AUDIO);

        if (Places.isInitialized()) {
            imageResIds.add(R.drawable.tap_ic_location_pumpkin_orange);
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

    public static List<TAPAttachmentModel> createImagePickerMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_camera_pumpkin_orange,
                R.drawable.tap_ic_gallery_pumpkin_orange,
        };

        int[] titleResIds = {
                R.string.tap_camera,
                R.string.tap_gallery,
        };

        int[] ids = {
                SELECT_PICTURE_CAMERA,
                SELECT_PICTURE_GALLERY,
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createFailedMessageBubbleLongPressMenu() {
        // TODO: 10 April 2019 ADD LONG PRESS MENU FOR FAILED MESSAGES
        int[] imageResIds = {

        };

        int[] titleResIds = {

        };

        int[] ids = {

        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createTextBubbleLongPressMenu(String instanceKey, TAPMessageModel messageModel) {

        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        imageResIds.add(R.drawable.tap_ic_reply_orange);
        titleResIds.add(R.string.tap_reply);
        ids.add(LONG_PRESS_REPLY);

        if (messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            imageResIds.add(R.drawable.tap_ic_forward_orange);
            titleResIds.add(R.string.tap_forward);
            ids.add(LONG_PRESS_FORWARD);
        }

        imageResIds.add(R.drawable.tap_ic_copy_orange);
        titleResIds.add(R.string.tap_copy);
        ids.add(LONG_PRESS_COPY);

        if (null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            imageResIds.add(R.drawable.tap_ic_delete_red_big);
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

        if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty() &&
                (TAPCacheManager.getInstance(TapTalk.appContext).containsCache((String) messageModel.getData().get(FILE_ID)) ||
                        null != messageModel.getData().get(FILE_URL))) {
            // Show Copy option to copy caption
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            imageResIds.add(R.drawable.tap_ic_save_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_copy);
            titleResIds.add(R.string.tap_save_to_gallery);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_COPY);
            ids.add(LONG_PRESS_SAVE_IMAGE_GALLERY);
        } else if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty()) {

            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_copy_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_copy);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_COPY);
        } else if (TAPCacheManager.getInstance(TapTalk.appContext).containsCache((String) messageModel.getData().get(FILE_ID))) {
            // Show only forward and reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_save_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_save_to_gallery);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_SAVE_IMAGE_GALLERY);
        } else {
            // Show only forward and reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageModel && null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            imageResIds.add(R.drawable.tap_ic_delete_red_big);
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

        if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty() &&
                TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
            // Show Copy option to copy caption
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_copy_orange);
            imageResIds.add(R.drawable.tap_ic_save_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_copy);
            titleResIds.add(R.string.tap_save_to_gallery);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_COPY);
            ids.add(LONG_PRESS_SAVE_VIDEO_GALLERY);
        } else if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty()) {

            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_copy_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_copy);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_COPY);
        } else if (TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
            // Show only forward and reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_save_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_save_to_gallery);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_SAVE_VIDEO_GALLERY);
        } else {
            // Show only forward and reply
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            titleResIds.add(R.string.tap_reply);
            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageModel && null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            imageResIds.add(R.drawable.tap_ic_delete_red_big);
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

        if (TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(messageModel)) {
            imageResIds.add(R.drawable.tap_ic_reply_orange);
            imageResIds.add(R.drawable.tap_ic_save_orange);

            titleResIds.add(R.string.tap_reply);
            titleResIds.add(R.string.tap_save_to_download);

            ids.add(LONG_PRESS_REPLY);
            ids.add(LONG_PRESS_SAVE_DOWNLOADS);
        } else {
            imageResIds.add(R.drawable.tap_ic_reply_orange);

            titleResIds.add(R.string.tap_reply);

            ids.add(LONG_PRESS_REPLY);
        }

        if (null != messageModel && null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            imageResIds.add(R.drawable.tap_ic_delete_red_big);
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

        imageResIds.add(R.drawable.tap_ic_reply_orange);
        if (messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            imageResIds.add(R.drawable.tap_ic_forward_orange);
        }
        imageResIds.add(R.drawable.tap_ic_copy_orange);

        titleResIds.add(R.string.tap_reply);
        if (messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            titleResIds.add(R.string.tap_forward);
        }
        titleResIds.add(R.string.tap_copy);

        ids.add(LONG_PRESS_REPLY);
        if (messageModel.getRoom().getRoomType() != TYPE_TRANSACTION) {
            ids.add(LONG_PRESS_FORWARD);
        }
        ids.add(LONG_PRESS_COPY);

        if (null != messageModel && null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                messageModel.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) &&
                null != messageModel.getSending() && !messageModel.getSending()) {
            imageResIds.add(R.drawable.tap_ic_delete_red_big);
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

    public static List<TAPAttachmentModel> createLinkLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_open_link_pumpkin_orange,
                R.drawable.tap_ic_copy_orange
        };

        int[] titleResIds = {
                R.string.tap_open,
                R.string.tap_copy
        };

        int[] ids = {
                LONG_PRESS_OPEN_LINK,
                LONG_PRESS_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createEmailLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_mail_pumpkin_orange,
                R.drawable.tap_ic_copy_orange
        };

        int[] titleResIds = {
                R.string.tap_compose,
                R.string.tap_copy
        };

        int[] ids = {
                LONG_PRESS_COMPOSE_EMAIL,
                LONG_PRESS_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createPhoneLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_call_pumpkin_orange,
                R.drawable.tap_ic_sms_pumpkin_orange,
                R.drawable.tap_ic_copy_orange
        };

        int[] titleResIds = {
                R.string.tap_call,
                R.string.tap_send_sms,
                R.string.tap_copy
        };

        int[] ids = {
                LONG_PRESS_CALL,
                LONG_PRESS_SEND_SMS,
                LONG_PRESS_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createMentionLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_members_grey,
                R.drawable.tap_ic_send_message_grey,
                R.drawable.tap_ic_copy_orange
        };

        int[] titleResIds = {
                R.string.tap_view_profile,
                R.string.tap_send_message,
                R.string.tap_copy
        };

        int[] ids = {
                LONG_PRESS_VIEW_PROFILE,
                LONG_PRESS_SEND_MESSAGE,
                LONG_PRESS_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }
}

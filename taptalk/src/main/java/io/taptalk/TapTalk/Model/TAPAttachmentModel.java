package io.taptalk.TapTalk.Model;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;

public class TAPAttachmentModel {
    private int icon;
    private int titleIds;
    private int id;

    public static final int ID_DOCUMENT = 1;
    public static final int ID_CAMERA = 2;
    public static final int ID_GALLERY = 3;
    public static final int ID_AUDIO = 4;
    public static final int ID_LOCATION = 5;
    public static final int ID_CONTACT = 6;
    public static final int ID_REPLY = 7;
    public static final int ID_FORWARD = 8;
    public static final int ID_COPY = 9;
    public static final int ID_OPEN = 10;
    public static final int ID_COMPOSE = 11;
    public static final int ID_CALL = 12;
    public static final int ID_SEND_SMS = 13;
    public static final int ID_SAVE_GALLERY = 14;
    public static final int ID_SAVE_DOWNLOAD = 15;

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
        // TODO: 31 January 2019 TEMPORARILY DISABLED FEATURE, REMOVED MENU FROM ATTACHMENT
        int[] imageResIds = {
                R.drawable.tap_ic_documents_pumpkin_orange,
                R.drawable.tap_ic_camera_pumpkin_orange,
                R.drawable.tap_ic_gallery_pumpkin_orange,
//                R.drawable.tap_ic_audio_pumpkin_orange,
                R.drawable.tap_ic_location_pumpkin_orange,
//                R.drawable.tap_ic_contact_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_document,
                R.string.tap_camera,
                R.string.tap_gallery,
//                R.string.audio,
                R.string.tap_location,
//                R.string.contact
        };

        int[] ids = {
                ID_DOCUMENT,
                ID_CAMERA,
                ID_GALLERY,
//                ID_AUDIO,
                ID_LOCATION,
//                ID_CONTACT
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
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
                ID_CAMERA,
                ID_GALLERY,
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

    public static List<TAPAttachmentModel> createTextBubbleLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_reply_pumpkin_orange,
                R.drawable.tap_ic_forward_pumpkin_orange,
                R.drawable.tap_ic_copy_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_reply,
                R.string.tap_forward,
                R.string.tap_copy
        };

        int[] ids = {
                ID_REPLY,
                ID_FORWARD,
                ID_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    public static List<TAPAttachmentModel> createImageBubbleLongPressMenu(TAPMessageModel messageModel) {

        int[] imageResIds, titleResIds, ids;

        if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty() &&
                TAPCacheManager.getInstance(TapTalk.appContext).containsCache((String) messageModel.getData().get(FILE_ID))) {
            // Show Copy option to copy caption
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
//                    R.drawable.tap_ic_forward_pumpkin_orange,
                    R.drawable.tap_ic_copy_pumpkin_orange,
                    R.drawable.tap_ic_download_pumpkin_orange
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
//                    R.string.tap_forward,
                    R.string.tap_copy,
                    R.string.tap_save_to_gallery
            };

            ids = new int[]{
                    ID_REPLY,
//                    ID_FORWARD,
                    ID_COPY,
                    ID_SAVE_GALLERY
            };
        } else if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty()) {

            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
//                    R.drawable.tap_ic_forward_pumpkin_orange,
                    R.drawable.tap_ic_copy_pumpkin_orange
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
//                    R.string.tap_forward,
                    R.string.tap_copy
            };

            ids = new int[]{
                    ID_REPLY,
//                    ID_FORWARD,
                    ID_COPY
            };
        } else if (TAPCacheManager.getInstance(TapTalk.appContext).containsCache((String) messageModel.getData().get(FILE_ID))) {
            // Show only forward and reply
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
                    R.drawable.tap_ic_download_pumpkin_orange
//                    R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
                    R.string.tap_save_to_gallery
//                    R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY,
                    ID_SAVE_GALLERY
//                    ID_FORWARD,
            };
        } else {
            // Show only forward and reply
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange
//                    R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply
//                    R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY
//                    ID_FORWARD,
            };
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createVideoBubbleLongPressMenu(TAPMessageModel messageModel) {

        int[] imageResIds, titleResIds, ids;

        if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty() &&
                null != TAPFileDownloadManager.getInstance().getFileMessageUri(messageModel.getRoom().getRoomID(),
                        (String) messageModel.getData().get(FILE_ID))) {
            // Show Copy option to copy caption
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
//                    R.drawable.tap_ic_forward_pumpkin_orange,
                    R.drawable.tap_ic_copy_pumpkin_orange,
                    R.drawable.tap_ic_download_pumpkin_orange
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
//                    R.string.tap_forward,
                    R.string.tap_copy,
                    R.string.tap_save_to_gallery
            };

            ids = new int[]{
                    ID_REPLY,
//                    ID_FORWARD,
                    ID_COPY,
                    ID_SAVE_GALLERY
            };
        } else if (null != messageModel.getData() &&
                null != messageModel.getData().get(CAPTION) &&
                !((String) messageModel.getData().get(CAPTION)).isEmpty()) {

            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
//                    R.drawable.tap_ic_forward_pumpkin_orange,
                    R.drawable.tap_ic_copy_pumpkin_orange
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
//                    R.string.tap_forward,
                    R.string.tap_copy
            };

            ids = new int[]{
                    ID_REPLY,
//                    ID_FORWARD,
                    ID_COPY
            };
        } else if (null != TAPFileDownloadManager.getInstance().getFileMessageUri(messageModel.getRoom().getRoomID(),
                (String) messageModel.getData().get(FILE_ID))) {
            // Show only forward and reply
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
                    R.drawable.tap_ic_download_pumpkin_orange
//                    R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
                    R.string.tap_save_to_gallery
//                    R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY,
                    ID_SAVE_GALLERY
//                    ID_FORWARD,
            };
        } else {
            // Show only forward and reply
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange
//                    R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply
//                    R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY
//                    ID_FORWARD,
            };
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    // TODO: 4 March 2019 TEMPORARILY DISABLED FORWARD
    public static List<TAPAttachmentModel> createFileBubbleLongPressMenu(TAPMessageModel messageModel) {

        int[] imageResIds, titleResIds, ids;

        if (null != TAPFileDownloadManager.getInstance().getFileMessageUri(messageModel.getRoom().getRoomID(),
                (String) messageModel.getData().get(FILE_ID))) {
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange,
                    R.drawable.tap_ic_download_pumpkin_orange,
//                R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply,
                    R.string.tap_save_to_download,
//                R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY,
                    ID_SAVE_DOWNLOAD,
//                ID_FORWARD,
            };
        } else {
            imageResIds = new int[]{
                    R.drawable.tap_ic_reply_pumpkin_orange
//                R.drawable.tap_ic_forward_pumpkin_orange,
            };

            titleResIds = new int[]{
                    R.string.tap_reply
//                R.string.tap_forward,
            };

            ids = new int[]{
                    ID_REPLY
//                ID_FORWARD,
            };
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createLocationBubbleLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_reply_pumpkin_orange,
                R.drawable.tap_ic_forward_pumpkin_orange,
                R.drawable.tap_ic_copy_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_reply,
                R.string.tap_forward,
                R.string.tap_copy
        };

        int[] ids = {
                ID_REPLY,
                ID_FORWARD,
                ID_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }

    public static List<TAPAttachmentModel> createLinkLongPressMenu() {
        int[] imageResIds = {
                R.drawable.tap_ic_open_link_pumpkin_orange,
                R.drawable.tap_ic_copy_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_open,
                R.string.tap_copy
        };

        int[] ids = {
                ID_OPEN,
                ID_COPY
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
                R.drawable.tap_ic_copy_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_compose,
                R.string.tap_copy
        };

        int[] ids = {
                ID_COMPOSE,
                ID_COPY
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
                R.drawable.tap_ic_copy_pumpkin_orange
        };

        int[] titleResIds = {
                R.string.tap_call,
                R.string.tap_send_sms,
                R.string.tap_copy
        };

        int[] ids = {
                ID_CALL,
                ID_SEND_SMS,
                ID_COPY
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.length;
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }
        return attachMenus;
    }
}

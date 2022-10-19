package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPAttachmentModel {
    private int icon;
    private int titleIds;
    private int id;
    private int imagePosition;

    public static final int SELECT_PICTURE_CAMERA = 1;
    public static final int SELECT_PICTURE_GALLERY = 2;

    public static final int ATTACH_DOCUMENT = 11;
    public static final int ATTACH_CAMERA = 12;
    public static final int ATTACH_GALLERY = 13;
    public static final int ATTACH_AUDIO = 14;
    public static final int ATTACH_LOCATION = 15;
    public static final int ATTACH_CONTACT = 16;

    public static final int SELECT_SET_AS_MAIN = 101;
    public static final int SELECT_SAVE_IMAGE = 102;
    public static final int SELECT_REMOVE_PHOTO = 103;

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

    public static final int LONG_PRESS_SAVE_PROFILE_PICTURE = 214;
    public static final int LONG_PRESS_STAR = 215;
    public static final int LONG_PRESS_EDIT = 216;
    public static final int LONG_PRESS_PIN = 217;
    public static final int LONG_PRESS_SHARED_MEDIA = 218;
    public static final int LONG_PRESS_SEND_NOW = 219;
    public static final int LONG_PRESS_RESCHEDULE = 220;
    // Check icon color in Attachment Adapter when adding more IDs

    public TAPAttachmentModel(int icon, int titleIds, int id) {
        this.icon = icon;
        this.titleIds = titleIds;
        this.id = id;
    }

    public static TAPAttachmentModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPAttachmentModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
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

    public int getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(int imagePosition) {
        this.imagePosition = imagePosition;
    }
}

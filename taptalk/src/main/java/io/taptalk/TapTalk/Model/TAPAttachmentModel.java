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
}

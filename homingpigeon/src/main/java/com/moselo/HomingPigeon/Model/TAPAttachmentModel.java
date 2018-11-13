package com.moselo.HomingPigeon.Model;

import com.moselo.HomingPigeon.R;

import java.util.ArrayList;
import java.util.List;

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
        int[] imageResIds = {
                R.drawable.tap_ic_documents_green_blue,
                R.drawable.tap_ic_camera_green_blue,
                R.drawable.tap_ic_gallery_green_blue,
                R.drawable.tap_ic_audio_green_blue,
                R.drawable.tap_ic_location_green_blue,
                R.drawable.tap_ic_contact_green_blue
        };

        int[] titleResIds = {
                R.string.document,
                R.string.camera,
                R.string.gallery,
                R.string.audio,
                R.string.location,
                R.string.contact
        };

        int[] ids = {
                ID_DOCUMENT,
                ID_CAMERA,
                ID_GALLERY,
                ID_AUDIO,
                ID_LOCATION,
                ID_CONTACT
        };

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        for (int index = 0; index < 6; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }

        return attachMenus;
    }
}

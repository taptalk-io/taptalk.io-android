package com.moselo.HomingPigeon.Model;

import com.moselo.HomingPigeon.R;

import java.util.ArrayList;
import java.util.List;

public class HpAttachmentModel {
    private int icon;
    private int titleIds;
    private int id;

    public HpAttachmentModel(int icon, int titleIds, int id) {
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

    public static List<HpAttachmentModel> createAttachMenu() {
        int[] imageResIds = {
                R.drawable.hp_ic_documents_green_blue,
                R.drawable.hp_ic_camera_green_blue,
                R.drawable.hp_ic_gallery_green_blue,
                R.drawable.hp_ic_audio_green_blue,
                R.drawable.hp_ic_location_green_blue,
                R.drawable.hp_ic_contact_green_blue
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
                R.id.bt_document,
                R.id.bt_camera,
                R.id.bt_gallery,
                R.id.bt_audio,
                R.id.bt_location,
                R.id.bt_contact
        };

        List<HpAttachmentModel> attachMenus = new ArrayList<>();
        for (int index = 0; index < 6; index++) {
            attachMenus.add(new HpAttachmentModel(imageResIds[index], titleResIds[index], ids[index]));
        }

        return attachMenus;
    }
}

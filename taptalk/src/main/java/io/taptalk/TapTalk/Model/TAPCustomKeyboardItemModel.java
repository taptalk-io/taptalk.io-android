package io.taptalk.TapTalk.Model;

import android.graphics.drawable.Drawable;

public class TAPCustomKeyboardItemModel {

    private Drawable iconImage;
    private String iconURL;
    private String itemName;
    private String itemID;

    public TAPCustomKeyboardItemModel(String itemID, Drawable iconImage, String itemName) {
        this.itemID = itemID;
        this.iconImage = iconImage;
        this.itemName = itemName;
    }

    public TAPCustomKeyboardItemModel(String itemID, String iconURL, String itemName) {
        this.itemID = itemID;
        this.iconURL = iconURL;
        this.itemName = itemName;
    }

    public Drawable getIconImage() {
        return iconImage;
    }

    public void setIconImage(Drawable iconImage) {
        this.iconImage = iconImage;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}

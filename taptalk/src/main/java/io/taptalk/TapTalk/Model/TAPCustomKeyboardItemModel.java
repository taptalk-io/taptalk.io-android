package io.taptalk.TapTalk.Model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPCustomKeyboardItemModel {

    @Nullable private Drawable iconImage;
    @Nullable private String iconURL;
    private String itemName;
    private String itemID;

    public TAPCustomKeyboardItemModel(String itemID, @Nullable Drawable iconImage, String itemName) {
        this.itemID = itemID;
        this.iconImage = iconImage;
        this.itemName = itemName;
    }

    public TAPCustomKeyboardItemModel(String itemID, @Nullable String iconURL, String itemName) {
        this.itemID = itemID;
        this.iconURL = iconURL;
        this.itemName = itemName;
    }

    public static TAPCustomKeyboardItemModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPCustomKeyboardItemModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    @Nullable
    public Drawable getIconImage() {
        return iconImage;
    }

    public void setIconImage(@Nullable Drawable iconImage) {
        this.iconImage = iconImage;
    }

    @Nullable
    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(@Nullable String iconURL) {
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

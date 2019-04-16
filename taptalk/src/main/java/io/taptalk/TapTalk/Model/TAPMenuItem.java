package io.taptalk.TapTalk.Model;

import io.taptalk.Taptalk.R;

public class TAPMenuItem {

    private int menuID, iconRes, textColorRes;
    private boolean isSwitchMenu, isChecked;
    private String menuLabel;

    public TAPMenuItem() {
    }

    public TAPMenuItem(int menuID, int iconRes, boolean isSwitchMenu, boolean isChecked, String menuLabel) {
        this.menuID = menuID;
        this.iconRes = iconRes;
        this.textColorRes = R.color.tap_black_44;
        this.isSwitchMenu = isSwitchMenu;
        this.isChecked = isChecked;
        this.menuLabel = menuLabel;
    }

    public TAPMenuItem(int menuID, int iconRes, int textColorRes, boolean isSwitchMenu, boolean isChecked, String menuLabel) {
        this.menuID = menuID;
        this.iconRes = iconRes;
        this.textColorRes = textColorRes;
        this.isSwitchMenu = isSwitchMenu;
        this.isChecked = isChecked;
        this.menuLabel = menuLabel;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getTextColorRes() {
        return textColorRes;
    }

    public void setTextColorRes(int textColorRes) {
        this.textColorRes = textColorRes;
    }

    public boolean isSwitchMenu() {
        return isSwitchMenu;
    }

    public void setSwitchMenu(boolean switchMenu) {
        isSwitchMenu = switchMenu;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public void setMenuLabel(String menuLabel) {
        this.menuLabel = menuLabel;
    }
}

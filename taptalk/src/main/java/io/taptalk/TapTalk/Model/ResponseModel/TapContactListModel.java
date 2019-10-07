package io.taptalk.TapTalk.Model.ResponseModel;

import android.support.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapContactListModel {

    public static final int TYPE_DEFAULT_CONTACT_LIST = 1;
    public static final int TYPE_SELECTABLE_CONTACT_LIST = 2;
    public static final int TYPE_SELECTED_GROUP_MEMBER = 3;
    public static final int TYPE_SECTION_TITLE = 4;
    public static final int TYPE_MENU_BUTTON = 5;
    public static final int TYPE_INFO_LABEL = 6;

    public static final int MENU_ID_NEW_CONTACT = 1;
    public static final int MENU_ID_SCAN_QR_CODE = 2;
    public static final int MENU_ID_CREATE_NEW_GROUP = 3;

    private String title;
    private int type;
    private int actionId;
    private int drawableResource;
    private boolean isSelected;
    @Nullable private TAPUserModel user;
    @Nullable private String buttonText;

    // Constructor for default contact list
    public TapContactListModel(TAPUserModel user) {
        this.title = user.getName();
        this.type = TYPE_DEFAULT_CONTACT_LIST;
        this.user = user;
    }

    // Constructor for selectable contact list or selected group member
    public TapContactListModel(TAPUserModel user, int type) {
        this.title = user.getName();
        this.type = type;
        this.user = user;
    }

    // Constructor for section title
    public TapContactListModel(String sectionTitle) {
        this.title = sectionTitle;
        this.type = TYPE_SECTION_TITLE;
    }

    // Constructor for menu item
    public TapContactListModel(String menuTitle, int actionId, int drawableResource) {
        this.title = menuTitle;
        this.buttonText = this.title;
        this.type = TYPE_MENU_BUTTON;
        this.actionId = actionId;
        this.drawableResource = drawableResource;
    }

    // Constructor for info label
    public TapContactListModel(String infoLabelTitle, int actionId, @Nullable String infoLabelButtonText) {
        this.title = infoLabelTitle;
        this.buttonText = infoLabelButtonText;
        this.type = TYPE_INFO_LABEL;
        this.actionId = actionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public void setDrawableResource(int drawableResource) {
        this.drawableResource = drawableResource;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Nullable
    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(@Nullable TAPUserModel user) {
        this.user = user;
    }

    @Nullable
    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(@Nullable String buttonText) {
        this.buttonText = buttonText;
    }
}

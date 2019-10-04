package io.taptalk.TapTalk.Model.ResponseModel;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapContactListModel {
    public static final int TYPE_DEFAULT_CONTACT_LIST = 11;
    public static final int TYPE_SELECTABLE_CONTACT_LIST = 12;
    public static final int TYPE_SELECTED_GROUP_MEMBER = 13;
    public static final int TYPE_SECTION_TITLE = 21;
    public static final int TYPE_MENU = 31;

    private String title;
    private TAPUserModel user;
    private int type;
    private int drawableResource;
    private boolean isSelected;

    // Constructor for default contact list
    public TapContactListModel(TAPUserModel user) {
        this.title = user.getName();
        this.user = user;
        this.type = TYPE_DEFAULT_CONTACT_LIST;
    }

    // Constructor for selectable contact list or selected group member
    public TapContactListModel(TAPUserModel user, int type) {
        this.title = user.getName();
        this.user = user;
        this.type = type;
    }

    // Constructor for section title
    public TapContactListModel(String sectionTitle) {
        this.title = sectionTitle;
        this.type = TYPE_SECTION_TITLE;
    }

    // Constructor for menu item
    public TapContactListModel(String menuTitle, int drawableResource) {
        this.title = menuTitle;
        this.type = TYPE_MENU;
        this.drawableResource = drawableResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}

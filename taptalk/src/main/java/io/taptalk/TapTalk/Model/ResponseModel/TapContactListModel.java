package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapContactListModel implements Parcelable {

    public static final int TYPE_DEFAULT_CONTACT_LIST = 1;
    public static final int TYPE_SELECTABLE_CONTACT_LIST = 2;
    public static final int TYPE_SELECTED_GROUP_MEMBER = 3;
    public static final int TYPE_SECTION_TITLE = 4;
    public static final int TYPE_MENU_BUTTON = 5;
    public static final int TYPE_INFO_LABEL = 6;

    public static final int MENU_ID_ADD_NEW_CONTACT = 1;
    public static final int MENU_ID_SCAN_QR_CODE = 2;
    public static final int MENU_ID_CREATE_NEW_GROUP = 3;

    public static final int INFO_LABEL_ID_VIEW_BLOCKED_CONTACTS = 101;
    public static final int INFO_LABEL_ID_ADD_NEW_CONTACT = 102;

    private String title;
    private int type;
    private int actionId;
    private int drawableResource;
    private boolean isSelected;
    @Nullable private TAPUserModel user;
    @Nullable private String buttonText;

    // Constructor for default contact list, selectable contact list, or selected group member
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
    public TapContactListModel(int actionId, String menuTitle, int drawableResource) {
        this.title = menuTitle;
        this.buttonText = this.title;
        this.type = TYPE_MENU_BUTTON;
        this.actionId = actionId;
        this.drawableResource = drawableResource;
    }

    // Constructor for info label
    public TapContactListModel(int actionId, String infoLabelTitle, @Nullable String infoLabelButtonText) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.actionId);
        dest.writeInt(this.drawableResource);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.buttonText);
    }

    protected TapContactListModel(Parcel in) {
        this.title = in.readString();
        this.type = in.readInt();
        this.actionId = in.readInt();
        this.drawableResource = in.readInt();
        this.isSelected = in.readByte() != 0;
        this.user = in.readParcelable(TAPUserModel.class.getClassLoader());
        this.buttonText = in.readString();
    }

    public static final Parcelable.Creator<TapContactListModel> CREATOR = new Parcelable.Creator<TapContactListModel>() {
        @Override
        public TapContactListModel createFromParcel(Parcel source) {
            return new TapContactListModel(source);
        }

        @Override
        public TapContactListModel[] newArray(int size) {
            return new TapContactListModel[size];
        }
    };
}

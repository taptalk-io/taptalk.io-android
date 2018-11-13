package com.moselo.HomingPigeon.Model;

import com.moselo.HomingPigeon.Helper.TapTalk;
import com.moselo.HomingPigeon.R;

public class TAPCustomKeyboardModel {

    public enum Type {
        SEE_PRICE_LIST,
        READ_EXPERT_NOTES,
        SEND_SERVICES,
        CREATE_ORDER
    }

    private Type type;
    private String icon, label;

    public TAPCustomKeyboardModel(Type type) {
        this.type = type;

        switch (type) {
            case SEE_PRICE_LIST:
                icon = TapTalk.appContext.getString(R.string.see_price_list_icon);
                label = TapTalk.appContext.getString(R.string.see_price_list);
                break;
            case READ_EXPERT_NOTES:
                icon = TapTalk.appContext.getString(R.string.read_expert_notes_icon);
                label = TapTalk.appContext.getString(R.string.read_expert_notes);
                break;
            case SEND_SERVICES:
                icon = TapTalk.appContext.getString(R.string.send_services_icon);
                label = TapTalk.appContext.getString(R.string.send_services);
                break;
            case CREATE_ORDER:
                icon = TapTalk.appContext.getString(R.string.create_order_card_icon);
                label = TapTalk.appContext.getString(R.string.create_order_card);
                break;
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

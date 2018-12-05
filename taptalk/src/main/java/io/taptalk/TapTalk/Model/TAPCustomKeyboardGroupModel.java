package io.taptalk.TapTalk.Model;

import java.util.List;

public class TAPCustomKeyboardGroupModel {

    private String senderRoleID;
    private String recipientRoleID;
    private List<TAPCustomKeyboardItemModel> customKeyboardItems;

    public TAPCustomKeyboardGroupModel(String senderRoleID, String recipientRoleID, List<TAPCustomKeyboardItemModel> customKeyboardItems) {
        this.senderRoleID = senderRoleID;
        this.recipientRoleID = recipientRoleID;
        this.customKeyboardItems = customKeyboardItems;
    }

    public String getSenderRoleID() {
        return senderRoleID;
    }

    public void setSenderRoleID(String senderRoleID) {
        this.senderRoleID = senderRoleID;
    }

    public String getRecipientRoleID() {
        return recipientRoleID;
    }

    public void setRecipientRoleID(String recipientRoleID) {
        this.recipientRoleID = recipientRoleID;
    }

    public List<TAPCustomKeyboardItemModel> getCustomKeyboardItems() {
        return customKeyboardItems;
    }

    public void setCustomKeyboardItems(List<TAPCustomKeyboardItemModel> customKeyboardItems) {
        this.customKeyboardItems = customKeyboardItems;
    }
}

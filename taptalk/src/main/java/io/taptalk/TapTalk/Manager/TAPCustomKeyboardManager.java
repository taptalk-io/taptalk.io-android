package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardGroupModel;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;

public class TAPCustomKeyboardManager {

    private static TAPCustomKeyboardManager instance;
    private Map<String, TAPCustomKeyboardGroupModel> masterCustomKeyboardItems;
    private List<TapTalkInterface> customKeyboardListeners;

    public TAPCustomKeyboardManager() {
    }

    public static TAPCustomKeyboardManager getInstance() {
        return null == instance ? (instance = new TAPCustomKeyboardManager()) : instance;
    }

    public void addCustomKeyboardItem(String senderRoleID, String recipientRoleID, List<TAPCustomKeyboardItemModel> customKeyboardItems) {
        getMasterCustomKeyboardItems().put(getMasterItemKey(senderRoleID, recipientRoleID), new TAPCustomKeyboardGroupModel(senderRoleID, recipientRoleID, customKeyboardItems));
    }

    public TAPCustomKeyboardGroupModel getCustomKeyboardItems(String senderRoleID, String recipientRoleID) {
        return getMasterCustomKeyboardItems().get(getMasterItemKey(senderRoleID, recipientRoleID));
    }

    public void addCustomKeyboardListener(TapTalkInterface listener) {
        if (null == customKeyboardListeners) {
            customKeyboardListeners = new ArrayList<>();
        }
        customKeyboardListeners.add(listener);
    }

    public void removeCustomKeyboardListener(TapTalkInterface listener) {
        if (null == customKeyboardListeners) {
            return;
        }
        customKeyboardListeners.remove(listener);
    }

    public void onCustomKeyboardItemClicked(String senderRoleID, String recipientRoleID, TAPCustomKeyboardItemModel customKeyboardItem) {
        for (TapTalkInterface listener : customKeyboardListeners) {
            listener.onCustomKeyboardItemClicked(senderRoleID, recipientRoleID, customKeyboardItem);
        }
    }

    private Map<String, TAPCustomKeyboardGroupModel> getMasterCustomKeyboardItems() {
        return null == masterCustomKeyboardItems ? masterCustomKeyboardItems = new LinkedHashMap<>() : masterCustomKeyboardItems;
    }

    private String getMasterItemKey(String senderRoleID, String recipientRoleID) {
        return senderRoleID + "-" + recipientRoleID;
    }
}

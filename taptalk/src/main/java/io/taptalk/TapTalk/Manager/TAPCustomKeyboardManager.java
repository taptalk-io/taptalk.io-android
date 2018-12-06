package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPCustomKeyboardManager {

    private static TAPCustomKeyboardManager instance;
    private List<TapTalkInterface> customKeyboardListeners;

    public TAPCustomKeyboardManager() {
    }

    public static TAPCustomKeyboardManager getInstance() {
        return null == instance ? (instance = new TAPCustomKeyboardManager()) : instance;
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

    public void onCustomKeyboardItemClicked(TAPCustomKeyboardItemModel customKeyboardItem, TAPUserModel activeUser, TAPUserModel otherUser) {
        for (TapTalkInterface listener : customKeyboardListeners) {
            listener.onCustomKeyboardItemClicked(customKeyboardItem, activeUser, otherUser);
        }
    }
}

package io.taptalk.TapTalk.Manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPCustomKeyboardManager {

    private static TAPCustomKeyboardManager instance;
    private List<TAPListener> customKeyboardListeners;

    public TAPCustomKeyboardManager() {
    }

    public static TAPCustomKeyboardManager getInstance() {
        return null == instance ? (instance = new TAPCustomKeyboardManager()) : instance;
    }

    public void addCustomKeyboardListener(TAPListener listener) {
        if (null == customKeyboardListeners) {
            customKeyboardListeners = new ArrayList<>();
        }
        customKeyboardListeners.add(listener);
    }

    public void removeCustomKeyboardListener(TAPListener listener) {
        if (null == customKeyboardListeners) {
            return;
        }
        customKeyboardListeners.remove(listener);
    }

    public void onCustomKeyboardItemClicked(Activity activity, TAPCustomKeyboardItemModel customKeyboardItem, TAPUserModel activeUser, TAPUserModel otherUser) {
        for (TAPListener listener : customKeyboardListeners) {
            listener.onCustomKeyboardItemClicked(activity, customKeyboardItem, activeUser, otherUser);
        }
    }
}

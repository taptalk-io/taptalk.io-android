package io.taptalk.TapTalk.Manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Listener.TapCustomKeyboardListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapCustomKeyboardManager {

    private static TapCustomKeyboardManager instance;

    private List<TapCustomKeyboardListener> customKeyboardListeners;

    public static TapCustomKeyboardManager getInstance() {
        return null == instance ? instance = new TapCustomKeyboardManager() : instance;
    }

    private List<TapCustomKeyboardListener> getCustomKeyboardListeners() {
        return null == customKeyboardListeners ? customKeyboardListeners = new ArrayList<>() : customKeyboardListeners;
    }

    public void addCustomKeyboardListener(TapCustomKeyboardListener listener) {
        getCustomKeyboardListeners().add(listener);
    }

    public void removeCustomKeyboardListener(TapCustomKeyboardListener listener) {
        getCustomKeyboardListeners().remove(listener);
    }

    List<TAPCustomKeyboardItemModel> getCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        for (TapCustomKeyboardListener listener : getCustomKeyboardListeners()) {
            List<TAPCustomKeyboardItemModel> items = listener.setCustomKeyboardItems(activeUser, otherUser);
            if (null != items && !items.isEmpty()) {
                return items;
            }
        }
        return null;
    }

    void triggerCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
        for (TapCustomKeyboardListener listener : getCustomKeyboardListeners()) {
            listener.onCustomKeyboardItemTapped(activity, customKeyboardItemModel, activeUser, otherUser);
        }
    }
}

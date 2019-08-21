package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapCustomKeyboardInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapCustomKeyboardListener implements TapCustomKeyboardInterface {

    @Override
    public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        return null;
    }

    @Override
    public void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItem, TAPUserModel activeUser, TAPUserModel otherUser) {
    }
}

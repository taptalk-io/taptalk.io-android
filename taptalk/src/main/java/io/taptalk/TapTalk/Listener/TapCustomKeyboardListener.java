package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.support.annotation.Nullable;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapCustomKeyboardInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapCustomKeyboardListener implements TapCustomKeyboardInterface {

    @Override
    public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
        return null;
    }

    @Override
    public void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItem, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
    }
}

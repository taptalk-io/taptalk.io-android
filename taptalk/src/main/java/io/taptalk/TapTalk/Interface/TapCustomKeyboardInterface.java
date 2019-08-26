package io.taptalk.TapTalk.Interface;

import android.app.Activity;
import android.support.annotation.Nullable;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapCustomKeyboardInterface {

    List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser);

    void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser);
}

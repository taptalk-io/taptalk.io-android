package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapCustomKeyboardInterface {

    List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser);

    void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser);
}

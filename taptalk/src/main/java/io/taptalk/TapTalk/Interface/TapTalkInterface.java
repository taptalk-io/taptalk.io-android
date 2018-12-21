package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkInterface {
    void onRefreshTokenExpiredOrInvalid();
    void onLoginSuccess(TAPUserModel myUserModel);
    void onUserProfileClicked(Activity activity, TAPUserModel userModel);
    void onCustomKeyboardItemClicked(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser);
    List<TAPCustomKeyboardItemModel> onRequestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser);
}

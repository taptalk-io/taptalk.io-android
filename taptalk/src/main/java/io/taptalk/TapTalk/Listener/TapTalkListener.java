package io.taptalk.TapTalk.Listener;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapTalkListener implements TapTalkInterface {
    @Override public void onRefreshTokenExpiredOrInvalid() {}
    @Override public void onLoginSuccess(TAPUserModel myUserModel) {}
    @Override public void onCustomKeyboardItemClicked(TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {}

    @Override
    public List<TAPCustomKeyboardItemModel> onRequestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        return null;
    }
}

package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;

public abstract class TapTalkListener implements TapTalkInterface {
    @Override public void onRefreshTokenExpiredOrInvalid() {}
    @Override public void onCustomKeyboardItemClicked(String senderRoleID, String recipientRoleID, TAPCustomKeyboardItemModel customKeyboardItemModel) {}
}

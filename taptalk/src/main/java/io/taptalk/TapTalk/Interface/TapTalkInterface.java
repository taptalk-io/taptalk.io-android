package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkInterface {
    void onRefreshTokenExpiredOrInvalid();
    void onCustomKeyboardItemClicked(String senderRoleID, String recipientRoleID, TAPCustomKeyboardItemModel customKeyboardItemModel);
    void onLoginSuccess(TAPUserModel myUserModel);
}

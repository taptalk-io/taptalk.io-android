package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;

public interface TapTalkInterface {
    void onRefreshTokenExpiredOrInvalid();
    void onCustomKeyboardItemClicked(String senderRoleID, String recipientRoleID, TAPCustomKeyboardItemModel customKeyboardItemModel);
}

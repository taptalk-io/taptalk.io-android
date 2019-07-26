package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkInterface {
    void onRefreshAuthTicket();
    void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount);
}

package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.HashMap;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapUIInterface {
    void onTapTalkChatRoomProfileButtonTapped(Activity activity, TAPUserModel userModel);

    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo);
}

package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.HashMap;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapUIInterface {
    void onTapTalkChatRoomProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user);

    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo);

    void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);

    void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption);
}

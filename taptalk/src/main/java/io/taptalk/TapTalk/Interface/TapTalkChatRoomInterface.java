package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkChatRoomInterface {
    void onTapTalkChatRoomProfileButtonTapped(Activity activity, TAPUserModel userModel);
    void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser);
    List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser);
    void onTapTalkProductListBubbleLeftButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room);
    void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room);
    void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo);
}

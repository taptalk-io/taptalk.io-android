package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.taptalk.TapTalk.Interface.TapUIChatRoomInterface;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public abstract class TapUIChatRoomListener implements TapUIChatRoomInterface {
    @Override
    public void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user) {
        openTapTalkChatProfile(activity, room);
    }

    @Override
    public void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room) {
        openTapTalkChatProfile(activity, room);
    }

    @Override
    public void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel message, HashMap<String, Object> userInfo) {

    }

    @Override
    public void onTapTalkProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }

    @Override
    public void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {

    }

    private void openTapTalkChatProfile(Activity activity, TAPRoomModel room) {
        if (null == activity) {
            return;
        }
        WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
        Intent intent = new Intent(contextWeakReference.get(), TAPChatProfileActivity.class);
        intent.putExtra(ROOM, room);
        if (room.getRoomType() == TYPE_PERSONAL) {
            contextWeakReference.get().startActivity(intent);
        } else if (room.getRoomType() == TYPE_GROUP) {
            contextWeakReference.get().startActivityForResult(intent, OPEN_PROFILE);
        }
        contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }
}

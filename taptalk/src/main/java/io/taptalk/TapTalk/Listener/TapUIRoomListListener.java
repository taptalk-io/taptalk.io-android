package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapUIRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;

@Keep
public abstract class TapUIRoomListListener implements TapUIRoomListInterface {

    private String instanceKey = "";

    public TapUIRoomListListener() {
    }

    public TapUIRoomListListener(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        if (null != mainRoomListFragment) {
            mainRoomListFragment.showSearchChat();
        }
    }

    @Override
    public void onCloseRoomListTapped(Activity activity) {
        if (null != activity) {
            activity.finish();
        }
    }

    @Override
    public void onTapTalkAccountButtonTapped(Activity activity) {
        TAPMyAccountActivity.Companion.start(activity, instanceKey);
    }

    @Override
    public void onNewChatButtonTapped(Activity activity) {
        TAPNewChatActivity.start(activity, instanceKey);
    }

    @Override
    public String setRoomListTitleText(TAPRoomListModel roomList, int position, Context context) {
        return TAPChatManager.getInstance(instanceKey).getDefaultRoomListTitleText(roomList, position, context);
    }

    @Override
    public String setRoomListContentText(TAPRoomListModel roomList, int position, Context context) {
        return TAPChatManager.getInstance(instanceKey).getDefaultRoomListContentText(roomList, position, context);
    }
}

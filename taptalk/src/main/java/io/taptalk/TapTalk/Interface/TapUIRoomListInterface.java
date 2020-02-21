package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;

public interface TapUIRoomListInterface {
    void onSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment);

    void onCloseRoomListTapped(Activity activity);

    void onTapTalkAccountButtonTapped(Activity activity);

    void onNewChatButtonTapped(Activity activity);
}

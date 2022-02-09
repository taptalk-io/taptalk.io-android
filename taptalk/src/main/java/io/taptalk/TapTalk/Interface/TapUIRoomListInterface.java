package io.taptalk.TapTalk.Interface;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;

public interface TapUIRoomListInterface {
    void onSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment);

    void onCloseRoomListTapped(Activity activity);

    void onTapTalkAccountButtonTapped(Activity activity);

    void onNewChatButtonTapped(Activity activity);

    String setRoomListTitleText(TAPRoomListModel roomList, int position, Context context);

    String setRoomListContentText(TAPRoomListModel roomList, int position, Context context);

}

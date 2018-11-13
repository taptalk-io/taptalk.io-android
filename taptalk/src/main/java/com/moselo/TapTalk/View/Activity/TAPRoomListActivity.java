package com.moselo.TapTalk.View.Activity;

import android.os.Bundle;

import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Model.TAPRoomModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.TAPRoomListFragment;
import com.moselo.HomingPigeon.View.Fragment.TAPSearchChatFragment;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_ROOM;

public class TAPRoomListActivity extends TAPBaseActivity {

    private static final String TAG = TAPRoomListActivity.class.getSimpleName();

    private enum RoomListState {
        STATE_SEARCH_CHAT, STATE_ROOM_LIST
    }

    private TAPRoomListFragment fRoomList;
    private TAPSearchChatFragment fSearchFragment;
    private RoomListState state = RoomListState.STATE_ROOM_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_room_list);
        initView();
    }

    @Override
    protected void initView() {
        fRoomList = (TAPRoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        fSearchFragment = (TAPSearchChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_search_chat);
        redirectToChatActivityFromNotification();
        showRoomList();
    }

    private void redirectToChatActivityFromNotification() {
        TAPRoomModel roomModel = getIntent().getParcelableExtra(K_ROOM);
        if (null != roomModel) {
            TAPUtils.getInstance().startChatActivity(this, roomModel);
        }
    }

    public void showRoomList() {
        state = RoomListState.STATE_ROOM_LIST;
        getSupportFragmentManager()
                .beginTransaction()
                .show(fRoomList)
                .hide(fSearchFragment)
                .commit();
    }

    public void showSearchChat() {
        state = RoomListState.STATE_SEARCH_CHAT;
        getSupportFragmentManager()
                .beginTransaction()
                .show(fSearchFragment)
                .hide(fRoomList)
                .commit();
    }

    @Override
    public void onBackPressed() {
        switch (state) {
            case STATE_ROOM_LIST:
                if (fRoomList.isSelecting()) {
                    fRoomList.cancelSelection();
                } else {
                    super.onBackPressed();
                }
                break;
            case STATE_SEARCH_CHAT:
                showRoomList();
                break;
        }
    }
}

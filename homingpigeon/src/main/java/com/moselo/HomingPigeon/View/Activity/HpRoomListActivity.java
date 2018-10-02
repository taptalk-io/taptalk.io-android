package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;

import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.HpRoomListFragment;
import com.moselo.HomingPigeon.View.Fragment.HpSearchChatFragment;

public class HpRoomListActivity extends HpBaseActivity {

    private static final String TAG = HpRoomListActivity.class.getSimpleName();

    private enum RoomListState {
        STATE_SEARCH_CHAT, STATE_ROOM_LIST
    }

    private HpRoomListFragment fRoomList;
    private HpSearchChatFragment fSearchFragment;
    private RoomListState state = RoomListState.STATE_ROOM_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_room_list);
        initView();
    }

    @Override
    protected void initView() {
        fRoomList = (HpRoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        fSearchFragment = (HpSearchChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_search_chat);
        showRoomList();
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
                super.onBackPressed();
                break;
            case STATE_SEARCH_CHAT:
                showRoomList();
                break;
        }
    }
}

package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;
import android.util.Log;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.RoomListFragment;
import com.moselo.HomingPigeon.View.Fragment.SearchChatFragment;

import java.io.IOException;
import java.net.URL;

public class RoomListActivity extends BaseActivity {

    private static final String TAG = RoomListActivity.class.getSimpleName();

    private enum RoomListState {
        STATE_SEARCH_CHAT, STATE_ROOM_LIST
    }

    private RoomListFragment fRoomList;
    private SearchChatFragment fSearchFragment;
    private RoomListState state = RoomListState.STATE_ROOM_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        initView();
    }

    @Override
    protected void initView() {
        fRoomList = (RoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        fSearchFragment = (SearchChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_search_chat);
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

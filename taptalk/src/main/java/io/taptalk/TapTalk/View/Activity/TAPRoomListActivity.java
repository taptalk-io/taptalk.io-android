package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.View.Fragment.TAPMainRoomListFragment;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;

public class TAPRoomListActivity extends TAPBaseActivity {

    private static final String TAG = TAPRoomListActivity.class.getSimpleName();
    private TAPMainRoomListFragment fRoomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_room_list);
        initView();
    }

    private void initView() {
        fRoomList = (TAPMainRoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        showRoomList();
        redirectToChatActivityFromNotification();
    }

    private void redirectToChatActivityFromNotification() {
        TAPRoomModel roomModel = getIntent().getParcelableExtra(ROOM);
        if (null != roomModel) {
            TAPUtils.getInstance().startChatActivity(this, roomModel);
        }
    }

    public void showRoomList() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(fRoomList)
                .commit();
    }

    @Override
    public void onBackPressed() {
        fRoomList.onBackPressed();
    }
}

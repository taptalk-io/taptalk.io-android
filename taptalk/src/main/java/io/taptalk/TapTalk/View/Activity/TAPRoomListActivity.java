package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;

import com.orhanobut.hawk.Hawk;

import io.taptalk.TapTalk.Helper.TAPAutoStartPermission;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.View.Fragment.TAPMainRoomListFragment;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;

public class TAPRoomListActivity extends TAPBaseActivity {

    private static final String TAG = TAPRoomListActivity.class.getSimpleName();
    public static final String AUTO_START_PERMISSION = "kAutoStartPermission";
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
        requestForAutoStartPermission();
    }

    private void redirectToChatActivityFromNotification() {
        TAPRoomModel roomModel = getIntent().getParcelableExtra(K_ROOM);
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

    //This is for Sample Apps
    private void requestForAutoStartPermission() {
        if (!Hawk.contains(AUTO_START_PERMISSION)) {
            TAPAutoStartPermission.getInstance().showPermissionRequest(this);
            Hawk.put(AUTO_START_PERMISSION, true);
        }
    }
}

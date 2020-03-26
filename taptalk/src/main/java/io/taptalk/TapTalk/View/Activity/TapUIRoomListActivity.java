package io.taptalk.TapTalk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.hawk.Hawk;

import io.taptalk.TapTalk.Helper.TAPAutoStartPermission;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;

public class TapUIRoomListActivity extends TAPBaseActivity {

    private static final String TAG = TapUIRoomListActivity.class.getSimpleName();
    public static final String AUTO_START_PERMISSION = "kAutoStartPermission";
    private TapUIMainRoomListFragment fRoomList;

    public static void start(
            Context context,
            String instanceKey
    ) {
        start(context, instanceKey, null);
    }

    public static void start(
            Context context,
            String instanceKey,
            TAPRoomModel room
    ) {
        start(context, instanceKey, room, false);
    }

    public static void start(
            Context context,
            String instanceKey,
            TAPRoomModel room,
            boolean flagNewTask
    ) {
        Intent intent = new Intent(context, TapUIRoomListActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        if (null != room) {
            intent.putExtra(ROOM, room);
        }
        if (flagNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_room_list);
        initView();
    }

    private void initView() {
        fRoomList = (TapUIMainRoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        fRoomList.setInstanceKey(instanceKey);
        showRoomList();
        redirectToChatActivityFromNotification();
        requestForAutoStartPermission();
    }

    private void redirectToChatActivityFromNotification() {
        TAPRoomModel roomModel = getIntent().getParcelableExtra(ROOM);
        if (null != roomModel) {
            TapUIChatActivity.start(this, instanceKey, roomModel);
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

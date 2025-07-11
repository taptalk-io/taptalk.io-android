package io.taptalk.TapTalk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import io.taptalk.TapTalk.Helper.TAPAutoStartPermission;
import io.taptalk.TapTalk.Helper.TapPreferenceUtils;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapUIRoomListActivity extends TAPBaseActivity {

    private static final String TAG = TapUIRoomListActivity.class.getSimpleName();
    public static final String AUTO_START_PERMISSION = "kAutoStartPermission";
    private TapUIMainRoomListFragment fRoomList;
    private boolean isResumed;

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

    public static void start(
        Context context,
        String instanceKey,
        String roomID,
        boolean flagNewTask
    ) {
        Intent intent = new Intent(context, TapUIRoomListActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        if (null != roomID && !roomID.isEmpty()) {
            intent.putExtra(ROOM_ID, roomID);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!isResumed) {
            isResumed = true;
            redirectToChatActivity(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        redirectToChatActivity(intent);
    }

    private void initView() {
//        fRoomList = (TapUIMainRoomListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_room_list);
        fRoomList = TapUIMainRoomListFragment.newInstance(instanceKey);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_room_list, fRoomList)
            .commit();
        showRoomList();
        requestForAutoStartPermission();
    }

    private void redirectToChatActivity(Intent intent) {
        Toast loadingToast = Toast.makeText(this, "Loading room…", Toast.LENGTH_SHORT);
        new Thread(() -> {
            TAPRoomModel roomModel = intent.getParcelableExtra(ROOM);
            if (null != roomModel) {
                runOnUiThread(() -> TapUIChatActivity.start(this, instanceKey, roomModel));
            }

            String roomID = intent.getStringExtra(ROOM_ID);
            if (roomID != null && !roomID.isEmpty()) {
                TAPRoomModel localRoom = TapCoreChatRoomManager.getInstance(instanceKey).getLocalChatRoomData(roomID);
                if (localRoom != null) {
                    runOnUiThread(() -> TapUI.getInstance(instanceKey).openChatRoomWithRoomModel(this, localRoom));
//                    Log.e(">>>>>>>>>>>>>>", "redirectToChatActivity: open room from local data");
                }
                else {
//                    Log.e(">>>>>>>>>>>>>>", "redirectToChatActivity: start fetch API");
                    runOnUiThread(() -> loadingToast.show());
                    TapCoreChatRoomManager.getInstance(instanceKey).getChatRoomData(roomID, new TapCoreGetRoomListener() {
                        @Override
                        public void onSuccess(TAPRoomModel room) {
                            runOnUiThread(() -> {
                                loadingToast.cancel();
                                TapUI.getInstance(instanceKey).openChatRoomWithRoomModel(TapUIRoomListActivity.this, room);
                            });
//                            Log.e(">>>>>>>>>>>>>>", "redirectToChatActivity: open room from API");
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            runOnUiThread(() -> {
                                loadingToast.cancel();
                                Toast.makeText(TapUIRoomListActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }).start();
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
        if (!TapPreferenceUtils.checkPreferenceKeyAvailable(AUTO_START_PERMISSION)) {
            TAPAutoStartPermission.getInstance().showPermissionRequest("", this);
            TapPreferenceUtils.saveBooleanPreference(AUTO_START_PERMISSION, true);
        }
    }
}

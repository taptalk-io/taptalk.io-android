package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Listener.RoomListListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.BarcodeScannerActivity;
import com.moselo.HomingPigeon.View.Activity.NewChatActivity;
import com.moselo.HomingPigeon.View.Activity.RoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.RoomListAdapter;
import com.moselo.HomingPigeon.View.Helper.Const;
import com.moselo.HomingPigeon.ViewModel.RoomListViewModel;

import java.util.Map;
import java.util.Objects;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class RoomListFragment extends Fragment {

    private String TAG = RoomListFragment.class.getSimpleName();
    private Activity activity;

    private ConstraintLayout clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llConnectionStatus, llRoomEmpty;
    private TextView tvSelectionCount, tvConnectionStatus;
    private ImageView ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore, ivConnectionStatus;
    private ProgressBar pbConnecting, pbSettingUp;
    private FloatingActionButton fabNewChat;

    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
    private RoomListListener roomListListener;
    private RoomListViewModel vm;

    public RoomListFragment() {
    }

    public static RoomListFragment newInstance() {
        Bundle args = new Bundle();
        RoomListFragment fragment = new RoomListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (RoomListActivity) getActivity();
        return inflater.inflate(R.layout.fragment_sample_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initListener();
        initView(view);
        initConnectionStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().removeSocketListener(socketListener);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(RoomListViewModel.class);
    }

    private void initListener() {
        roomListListener = (messageModel, isSelected) -> {
            if (null != messageModel && isSelected) {
                vm.getSelectedRooms().put(messageModel.getLocalID(), messageModel);
            } else if (null != messageModel) {
                vm.getSelectedRooms().remove(messageModel.getLocalID());
            }
            if (vm.getSelectedCount() > 0) {
                showSelectionActionBar();
            } else {
                hideSelectionActionBar();
            }
        };
    }

    private void initView(View view) {
        // Dummy Rooms
        if (vm.getRoomList().size() == 0) {
            setDummyRooms();
        }
        // End Dummy

        Objects.requireNonNull(getActivity()).getWindow().setBackgroundDrawable(null);

        clButtonSearch = view.findViewById(R.id.cl_button_search);
        clSelection = view.findViewById(R.id.cl_selection);
        flSetupContainer = view.findViewById(R.id.fl_setup_container);
        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        llRoomEmpty = view.findViewById(R.id.ll_room_empty);
        tvSelectionCount = view.findViewById(R.id.tv_selection_count);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        ivButtonMute = view.findViewById(R.id.iv_button_mute);
        ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        ivButtonMore = view.findViewById(R.id.iv_button_more);
        ivConnectionStatus = view.findViewById(R.id.iv_connection_status);
        pbConnecting = view.findViewById(R.id.pb_connecting);
        pbSettingUp = view.findViewById(R.id.pb_setting_up);
        fabNewChat = view.findViewById(R.id.fab_new_chat);
        rvContactList = view.findViewById(R.id.rv_contact_list);

        pbConnecting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        pbSettingUp.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.amethyst), PorterDuff.Mode.SRC_IN);

        // TODO: 12 September 2018 HANDLE SETUP CHAT
        if (vm.getRoomList().size() == 0) {
            fabNewChat.setVisibility(View.GONE);
            new Handler().postDelayed(() -> flSetupContainer.animate()
                    .alpha(0)
                    .setDuration(300L)
                    .withEndAction(() -> {
                        flSetupContainer.setVisibility(View.GONE);
                        fabNewChat.setVisibility(View.VISIBLE);
                    }).start(), 2000L);
        } else {
            flSetupContainer.setVisibility(View.GONE);
        }

        if (vm.isSelecting()) showSelectionActionBar();

        adapter = new RoomListAdapter(vm, activity.getIntent().getStringExtra(Const.K_MY_USERNAME), roomListListener);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        if (0 == vm.getRoomList().size()) {
            llRoomEmpty.setVisibility(View.VISIBLE);
        } else {
            llRoomEmpty.setVisibility(View.GONE);
        }

        clButtonSearch.setOnClickListener(v -> {
        });

        fabNewChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewChatActivity.class);
            startActivity(intent);
        });

        ivButtonCancelSelection.setOnClickListener(v -> {
            for (Map.Entry<String, MessageModel> entry : vm.getSelectedRooms().entrySet()) {
                entry.getValue().getRoom().setSelected(false);
            }
            vm.getSelectedRooms().clear();
            adapter.notifyDataSetChanged();
            hideSelectionActionBar();
        });

        ivButtonMute.setOnClickListener(v -> {

        });

        ivButtonDelete.setOnClickListener(v -> {

        });

        ivButtonMore.setOnClickListener(v -> {

        });

        flSetupContainer.setOnClickListener(v -> {
        });
    }

    private void initConnectionStatus() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            socketListener.onSocketDisconnected();
    }

    private void showSelectionActionBar() {
        vm.setSelecting(true);
        tvSelectionCount.setText(vm.getSelectedCount() + "");
        clButtonSearch.setElevation(0);
        clButtonSearch.setVisibility(View.INVISIBLE);
        clSelection.setVisibility(View.VISIBLE);
    }

    private void hideSelectionActionBar() {
        vm.setSelecting(false);
        clButtonSearch.setElevation(Utils.getInstance().dpToPx(2));
        clButtonSearch.setVisibility(View.VISIBLE);
        clSelection.setVisibility(View.INVISIBLE);
    }

    private void setStatusConnected() {
        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connected);
            tvConnectionStatus.setText(getString(R.string.connected));
            ivConnectionStatus.setImageResource(R.drawable.ic_connected_white);
            ivConnectionStatus.setVisibility(View.VISIBLE);
            pbConnecting.setVisibility(View.GONE);
            llConnectionStatus.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> llConnectionStatus.setVisibility(View.GONE), 500L);
        });
    }

    private void setStatusConnecting() {
        if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext())) return;

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connecting);
            tvConnectionStatus.setText(R.string.connecting);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    private void setStatusWaitingForNetwork() {
        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_offline);
            tvConnectionStatus.setText(R.string.waiting_for_network);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    // Update connection status UI
    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {

        }

        @Override
        public void onSocketConnected() {
            setStatusConnected();
        }

        @Override
        public void onSocketDisconnected() {
            setStatusWaitingForNetwork();
        }

        @Override
        public void onSocketConnecting() {
            setStatusConnecting();
        }

        @Override
        public void onSocketError() {
            setStatusWaitingForNetwork();
        }
    };


    // TODO: 14/09/18 Harus dihilangkan pas ada flow fixnya
    private void setDummyRooms() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, ""));
        String userId = myUser.getUserID();

        RoomModel room1 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "1"), 1);
        RoomModel room2 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "2"), 1);
        RoomModel room3 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "3"), 1);
        RoomModel room4 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "4"), 1);
        RoomModel room5 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "5"), 1);
        RoomModel room6 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "6"), 1);
        RoomModel room7 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "7"), 1);
        RoomModel room8 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "8"), 1);
        RoomModel room9 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "9"), 1);
        RoomModel room10 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "10"), 1);
        RoomModel room11 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "11"), 1);
        RoomModel room12 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "12"), 1);
        RoomModel room13 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "13"), 1);
        RoomModel room14 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "14"), 1);
        RoomModel room15 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "15"), 1);
        room1.setUnreadCount(11);
        room2.setMuted(true);
        room2.setUnreadCount(999);

        UserModel dummyUser1 = new UserModel("1", "Ritchie Nathaniel");
        MessageModel roomDummy1 = new MessageModel(
                "", "def456",
                "",
                room1,
                1,
                9999L,
                dummyUser1,"1",
                0, 0, 0);

        UserModel dummyUser2 = new UserModel("2", "Dominic Vedericho");
        MessageModel roomDummy2 = new MessageModel(
                "", "def456",
                "",
                room2,
                1,
                9999L,
                dummyUser2,"2",
                0, 0, 0);

        UserModel dummyUser3 = new UserModel("3", "Rionaldo Aureri Linggautama");
        MessageModel roomDummy3 = new MessageModel(
                "", "def456",
                "",
                room3,
                1,
                9999L,
                dummyUser3,"3",
                0, 0, 0);

        UserModel dummyUser4 = new UserModel("4", "Kevin Reynaldo");
        MessageModel roomDummy4 = new MessageModel(
                "", "def456",
                "",
                room4,
                1,
                9999L,
                dummyUser4,"4",
                0, 0, 0);

        UserModel dummyUser5 = new UserModel("5", "Welly Kencana");
        MessageModel roomDummy5 = new MessageModel(
                "", "def456",
                "",
                room5,
                1,
                9999L,
                dummyUser5,"5",
                0, 0, 0);

        UserModel dummyUser6 = new UserModel("6", "Jony");
        MessageModel roomDummy6 = new MessageModel(
                "", "def456",
                "",
                room6,
                1,
                9999L,
                dummyUser6,"6",
                0, 0, 0);

        UserModel dummyUser7 = new UserModel("7", "Michael Tansy");
        MessageModel roomDummy7 = new MessageModel(
                "", "def456",
                "",
                room7,
                1,
                9999L,
                dummyUser7,"7",
                0, 0, 0);

        UserModel dummyUser8 = new UserModel("8", "Richard Fang");
        MessageModel roomDummy8 = new MessageModel(
                "", "def456",
                "",
                room8,
                1,
                9999L,
                dummyUser8,"8",
                0, 0, 0);

        UserModel dummyUser9 = new UserModel("9", "Erwin Andreas");
        MessageModel roomDummy9 = new MessageModel(
                "", "def456",
                "",
                room9,
                1,
                9999L,
                dummyUser9,"9",
                0, 0, 0);

        UserModel dummyUser10 = new UserModel("10", "Jefry Lorentono");
        MessageModel roomDummy10 = new MessageModel(
                "", "def456",
                "",
                room10,
                1,
                9999L,
                dummyUser10,"10",
                0, 0, 0);

        UserModel dummyUser11 = new UserModel("11", "Cundy Sunardy");
        MessageModel roomDummy11 = new MessageModel(
                "", "def456",
                "",
                room11,
                1,
                9999L,
                dummyUser11,"11",
                0, 0, 0);

        UserModel dummyUser12 = new UserModel("12", "Rizka Fatmawati");
        MessageModel roomDummy12 = new MessageModel(
                "", "def456",
                "",
                room12,
                1,
                9999L,
                dummyUser12,"12",
                0, 0, 0);

        UserModel dummyUser13 = new UserModel("13", "Test 1");
        MessageModel roomDummy13 = new MessageModel(
                "", "def456",
                "",
                room13,
                1,
                9999L,
                dummyUser13,"13",
                0, 0, 0);

        UserModel dummyUser14 = new UserModel("14", "Test 2");
        MessageModel roomDummy14 = new MessageModel(
                "", "def456",
                "",
                room14,
                1,
                9999L,
                dummyUser14,"14",
                0, 0, 0);

        UserModel dummyUser15 = new UserModel("15", "Test 3");
        MessageModel roomDummy15 = new MessageModel(
                "", "def456",
                "",
                room15,
                1,
                9999L,
                dummyUser15,"15",
                0, 0, 0);

        vm.getRoomList().add(roomDummy1);
        vm.getRoomList().add(roomDummy2);
        vm.getRoomList().add(roomDummy3);
        vm.getRoomList().add(roomDummy4);
        vm.getRoomList().add(roomDummy5);
        vm.getRoomList().add(roomDummy6);
        vm.getRoomList().add(roomDummy7);
        vm.getRoomList().add(roomDummy8);
        vm.getRoomList().add(roomDummy9);
        vm.getRoomList().add(roomDummy10);
        vm.getRoomList().add(roomDummy11);
        vm.getRoomList().add(roomDummy12);
        vm.getRoomList().add(roomDummy13);
        vm.getRoomList().add(roomDummy14);
        vm.getRoomList().add(roomDummy15);
    }
}

package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
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
import com.moselo.HomingPigeon.View.Activity.SampleRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.RoomListAdapter;
import com.moselo.HomingPigeon.View.Helper.Const;
import com.moselo.HomingPigeon.ViewModel.RoomListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class SampleRoomListFragment extends Fragment {

    private String TAG = SampleRoomListFragment.class.getSimpleName();
    private Activity activity;
    private ConstraintLayout clButtonSearch, clSelection;
    private LinearLayout llConnectionStatus;
    private TextView tvSelectionAmount, tvConnectionStatus;
    private ImageView ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore, ivConnectionStatus;
    private ProgressBar pbConnecting;
    private FloatingActionButton fabNewChat;
    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
    private RoomListListener roomListListener;
    private RoomListViewModel vm;
//    private TransitionDrawable connectionStatusBackground;
//    private Drawable connectionStatusDrawables[];

    public SampleRoomListFragment() {
    }

    public static SampleRoomListFragment newInstance() {
        Bundle args = new Bundle();
        SampleRoomListFragment fragment = new SampleRoomListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SampleRoomListActivity) getActivity();
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
            } else if(null != messageModel) {
                vm.getSelectedRooms().remove(messageModel.getLocalID());
            }

            if (vm.getSelectedCount() > 0) {
                vm.setSelecting(true);
                clButtonSearch.setElevation(0);
                clButtonSearch.setVisibility(View.INVISIBLE);
                clSelection.setVisibility(View.VISIBLE);
            } else {
                vm.setSelecting(false);
                clButtonSearch.setElevation(Utils.getInstance().dpToPx(2));
                clButtonSearch.setVisibility(View.VISIBLE);
                clSelection.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void initView(View view) {
        // Dummy Rooms
        if (vm.getRoomList().size() == 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
            }, prefs.getString(K_USER, ""));
            String userId = myUser.getUserID();
            RoomModel room1 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, userId), 1);
            RoomModel room2 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "999999"), 1);
            MessageModel roomDummy1 = new MessageModel(
                    "", "abc123",
                    "LastMessage",
                    room1,
                    1,
                    System.currentTimeMillis() / 1000,
                    myUser,
                    0, 0, 0);
            UserModel dummyUser2 = new UserModel("999999", "BAMBANGS");
            MessageModel roomDummy2 = new MessageModel(
                    "", "def456",
                    "Mas Bambang Mas Bambang Mas Bambang Mas Bambang Mas Bambang Mas Bambang.",
                    room2,
                    1,
                    0L,
                    dummyUser2,
                    0, 0, 0);
            vm.getRoomList().add(roomDummy1);
            vm.getRoomList().add(roomDummy2);
        }
        // End Dummy

        Objects.requireNonNull(getActivity()).getWindow().setBackgroundDrawable(null);

        clButtonSearch = view.findViewById(R.id.cl_button_search);
        clSelection = view.findViewById(R.id.cl_selection);
        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        tvSelectionAmount = view.findViewById(R.id.tv_selection_amount);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        ivButtonMute = view.findViewById(R.id.iv_button_mute);
        ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        ivButtonMore = view.findViewById(R.id.iv_button_more);
        ivConnectionStatus = view.findViewById(R.id.iv_connection_status);
        pbConnecting = view.findViewById(R.id.pb_connecting);
        fabNewChat = view.findViewById(R.id.fab_new_chat);

//        connectionStatusDrawables = new Drawable[4];
//        connectionStatusDrawables[0] = activity.getDrawable(R.drawable.bg_status_connected);
//        connectionStatusDrawables[1] = activity.getDrawable(R.drawable.bg_status_connecting);
//        connectionStatusDrawables[2] = activity.getDrawable(R.drawable.bg_status_offline);
//        connectionStatusDrawables[3] = activity.getDrawable(R.drawable.bg_status_error);
//        connectionStatusBackground = new TransitionDrawable(connectionStatusDrawables);
//        llConnectionStatus.setBackground(connectionStatusBackground);
        pbConnecting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        adapter = new RoomListAdapter(vm, activity.getIntent().getStringExtra(Const.K_MY_USERNAME), roomListListener);
        rvContactList = view.findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);

        clButtonSearch.setOnClickListener(v -> {

        });

        fabNewChat.setOnClickListener(v -> {

        });

        ivButtonCancelSelection.setOnClickListener(v -> {
            for (Map.Entry<String, MessageModel> entry : vm.getSelectedRooms().entrySet()) {
                entry.getValue().getRoom().setSelected(false);
            }
            vm.getSelectedRooms().clear();
            roomListListener.onRoomSelected(null, false);
            adapter.notifyDataSetChanged();
        });

        ivButtonMute.setOnClickListener(v -> {

        });

        ivButtonDelete.setOnClickListener(v -> {

        });

        ivButtonMore.setOnClickListener(v -> {

        });
    }

    private void initConnectionStatus() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            socketListener.onSocketDisconnected();
    }

    // Update connection status UI
    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {

        }

        @Override
        public void onSocketConnected() {
            activity.runOnUiThread(() -> {
                llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connected);
                tvConnectionStatus.setText(R.string.connected);
                ivConnectionStatus.setImageResource(R.drawable.ic_connected_white);
                ivConnectionStatus.setVisibility(View.VISIBLE);
                pbConnecting.setVisibility(View.GONE);
                llConnectionStatus.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> llConnectionStatus.setVisibility(View.GONE), 500L);
            });
        }

        @Override
        public void onSocketDisconnected() {
            activity.runOnUiThread(() -> {
                llConnectionStatus.setBackgroundResource(R.drawable.bg_status_offline);
                tvConnectionStatus.setText(R.string.offline);
                ivConnectionStatus.setImageResource(R.drawable.ic_offline_white);
                ivConnectionStatus.setVisibility(View.VISIBLE);
                pbConnecting.setVisibility(View.GONE);
                llConnectionStatus.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public void onSocketConnecting() {
            if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext())) return;

            activity.runOnUiThread(() -> {
                llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connecting);
                tvConnectionStatus.setText(R.string.connecting);
                ivConnectionStatus.setVisibility(View.GONE);
                pbConnecting.setVisibility(View.VISIBLE);
                llConnectionStatus.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public void onSocketError() {
            activity.runOnUiThread(() -> {
                llConnectionStatus.setBackgroundResource(R.drawable.bg_status_error);
                tvConnectionStatus.setText(R.string.network_error);
                ivConnectionStatus.setImageResource(R.drawable.ic_network_error_white);
                ivConnectionStatus.setVisibility(View.VISIBLE);
                pbConnecting.setVisibility(View.GONE);
                llConnectionStatus.setVisibility(View.VISIBLE);
            });
        }
    };
}

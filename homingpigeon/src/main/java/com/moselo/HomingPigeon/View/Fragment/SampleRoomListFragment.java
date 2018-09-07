package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class SampleRoomListFragment extends Fragment {

    private String TAG = SampleRoomListFragment.class.getSimpleName();
    private Activity activity;
    private ConstraintLayout clButtonSearch;
    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;
    private ProgressBar pbConnecting;
    private FloatingActionButton fabNewChat;
    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
//    private TransitionDrawable connectionStatusBackground;
//    private Drawable connectionStatusDrawables[];

    private List<MessageModel> roomList;

    public SampleRoomListFragment() {
        roomList = new ArrayList<>();
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
        initView(view);
        initConnectionStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().removeSocketListener(socketListener);
    }

    private void initView(View view) {
        // Dummy Rooms
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, ""));
        String userId = myUser.getUserID();
        RoomModel room1 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, userId), 1);
        RoomModel room2 = new RoomModel(ChatManager.getInstance().arrangeRoomId(userId, "999999"), 1);
        MessageModel roomDummy1 = new MessageModel(
                "", "",
                "LastMessage",
                room1,
                1,
                System.currentTimeMillis() / 1000,
                myUser,
                0, 0, 0);
        UserModel dummyUser2 = new UserModel("999999", "BAMBANGS");
        MessageModel roomDummy2 = new MessageModel(
                "", "",
                "Mas Bambang Mas Bambang Mas Bambang Mas Bambang Mas Bambang Mas Bambang.",
                room2,
                1,
                0L,
                dummyUser2,
                0, 0, 0);
        roomList.add(roomDummy1);
        roomList.add(roomDummy2);
        // End Dummy

        Objects.requireNonNull(getActivity()).getWindow().setBackgroundDrawable(null);

        clButtonSearch = view.findViewById(R.id.cl_button_search);
        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
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

        adapter = new RoomListAdapter(roomList, activity.getIntent().getStringExtra(Const.K_MY_USERNAME));
        rvContactList = view.findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);

        clButtonSearch.setOnClickListener(searchButtonClickListener);
        fabNewChat.setOnClickListener(newChatButtonListener);
    }

    private void initConnectionStatus() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            socketListener.onSocketDisconnected();
    }

    View.OnClickListener searchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener newChatButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    // Update connection status UI
    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {

        }

        @Override
        public void onSocketConnected() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connected);
                    tvConnectionStatus.setText(R.string.connected);
                    ivConnectionStatus.setImageResource(R.drawable.ic_connected_white);
                    ivConnectionStatus.setVisibility(View.VISIBLE);
                    pbConnecting.setVisibility(View.GONE);
                    llConnectionStatus.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llConnectionStatus.setVisibility(View.GONE);
                        }
                    }, 500L);
                }
            });
        }

        @Override
        public void onSocketDisconnected() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llConnectionStatus.setBackgroundResource(R.drawable.bg_status_offline);
                    tvConnectionStatus.setText(R.string.offline);
                    ivConnectionStatus.setImageResource(R.drawable.ic_offline_white);
                    ivConnectionStatus.setVisibility(View.VISIBLE);
                    pbConnecting.setVisibility(View.GONE);
                    llConnectionStatus.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onSocketConnecting() {
            if (!NetworkStateManager.getInstance().hasNetworkConnection(getContext())) return;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connecting);
                    tvConnectionStatus.setText(R.string.connecting);
                    ivConnectionStatus.setVisibility(View.GONE);
                    pbConnecting.setVisibility(View.VISIBLE);
                    llConnectionStatus.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onSocketError() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llConnectionStatus.setBackgroundResource(R.drawable.bg_status_error);
                    tvConnectionStatus.setText(R.string.network_error);
                    ivConnectionStatus.setImageResource(R.drawable.ic_network_error_white);
                    ivConnectionStatus.setVisibility(View.VISIBLE);
                    pbConnecting.setVisibility(View.GONE);
                    llConnectionStatus.setVisibility(View.VISIBLE);
                }
            });
        }
    };
}

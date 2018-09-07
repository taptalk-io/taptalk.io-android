package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ChatManager;
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

    private Activity activity;
    private ConstraintLayout clButtonSearch;
    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;
    private ProgressBar pbConnecting;
    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
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

        adapter = new RoomListAdapter(roomList, activity.getIntent().getStringExtra(Const.K_MY_USERNAME));
        rvContactList = view.findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);

        clButtonSearch.setOnClickListener(searchClickListener);
    }

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: 7 September 2018 START ACTIVITY
        }
    };

    // TODO: 7 September 2018 CONNECTION STATUS CALLBACK
}

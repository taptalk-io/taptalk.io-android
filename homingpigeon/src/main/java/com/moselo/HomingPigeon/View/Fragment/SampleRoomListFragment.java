package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.SampleRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.RoomListAdapter;
import com.moselo.HomingPigeon.View.Helper.Const;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class SampleRoomListFragment extends Fragment {

    private Activity activity;
    private LinearLayout llButtonSearch, llConnectionStatus;
    private TextView tvButtonEdit, tvConnectionStatus;
    private ImageView ivButtonNewChat, ivConnectionStatus;
    private ProgressBar pbConnecting;
    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
    private List<MessageEntity> roomList;

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
        //Dummy Rooms
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, prefs.getString(K_USER, ""));
        String userId = myUser.getUserID();
        MessageEntity roomDummy1 = new MessageEntity(
                "", "",
                ChatManager.getInstance().arrangeRoomId(userId, userId),
                1,
                "LastMessage",
                System.currentTimeMillis()/1000,
                prefs.getString(K_USER,"{}"));
        String dummyUser2 = Utils.getInstance().toJsonString(new UserModel("0", "BAMBANGS"));
        MessageEntity roomDummy2 = new MessageEntity(
                "", "",
                ChatManager.getInstance().arrangeRoomId(userId, "0"),
                1,
                "mas bambang's room",
                0,
                dummyUser2);
        roomList.add(roomDummy1);
        roomList.add(roomDummy2);

        llButtonSearch = view.findViewById(R.id.ll_button_search);
        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        tvButtonEdit = view.findViewById(R.id.tv_button_edit);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        ivButtonNewChat = view.findViewById(R.id.iv_button_new_chat);
        ivConnectionStatus = view.findViewById(R.id.iv_connection_status);
        pbConnecting = view.findViewById(R.id.pb_connecting);

        adapter = new RoomListAdapter(roomList, activity.getIntent().getStringExtra(Const.K_MY_USERNAME));
        rvContactList = view.findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);
    }
}

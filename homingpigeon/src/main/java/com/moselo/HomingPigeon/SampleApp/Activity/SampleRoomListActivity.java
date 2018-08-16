package com.moselo.HomingPigeon.SampleApp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.SampleApp.Adapter.RoomListAdapter;
import com.moselo.HomingPigeon.SampleApp.Helper.Const;

import java.util.ArrayList;
import java.util.List;

public class SampleRoomListActivity extends AppCompatActivity {

    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
    private List<MessageEntity> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_room_list);

        initView();
    }

    private void initView() {
        roomList = new ArrayList<>();

        //Dummy Rooms
        MessageEntity roomDummy1 = new MessageEntity(getIntent().getStringExtra(Const.K_MY_USERNAME), 1, "Last Message");
        roomList.add(roomDummy1);

        adapter = new RoomListAdapter(roomList, getIntent().getStringExtra(Const.K_MY_USERNAME));
        rvContactList = findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);
    }
}

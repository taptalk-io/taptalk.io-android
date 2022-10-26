package io.moselo.SampleApps.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalkSample.R;

public class MeetTalkChatRoomNavigationBarFragment extends TapBaseChatRoomCustomNavigationBarFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meettalk_chat_room_navigation_bar, container, false);
    }

    public MeetTalkChatRoomNavigationBarFragment(TAPRoomModel room) {
        super(room);
    }

    @Override
    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {
        Log.e(">>>>>>>>>>", "Test Fragment onReceiveUpdatedChatRoomData room: " + room.getName());
        Log.e(">>>>>>>>>>", "Test Fragment onReceiveUpdatedChatRoomData recipientUser: " + (recipientUser != null ? recipientUser.getFullname() : "null"));
    }

    @Override
    public void onReceiveStartTyping(String roomID, TAPUserModel user) {
        Log.e(">>>>>>>>>>", "Test Fragment onReceiveStartTyping: " + roomID + " - " + user.getFullname());
    }

    @Override
    public void onReceiveStopTyping(String roomID, TAPUserModel user) {
        Log.e(">>>>>>>>>>", "Test Fragment onReceiveStopTyping: " + roomID + " - " + user.getFullname());
    }

    @Override
    public void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive) {
        Log.e(">>>>>>>>>>", "Test Fragment onReceiveOnlineStatus: " + user.getFullname() + " - " + isOnline);
    }
}

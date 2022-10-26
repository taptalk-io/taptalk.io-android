package io.taptalk.TapTalk.View.Fragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TapCoreChatRoomListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;

public class TapBaseChatRoomCustomNavigationBarFragment extends Fragment {

    private TAPRoomModel room;

    public TapBaseChatRoomCustomNavigationBarFragment() {
        
    }

    public TapBaseChatRoomCustomNavigationBarFragment(TAPRoomModel room) {
        this.room = room;
    }

    @Nullable
    public String getInstanceKey() {
        if (getActivity() != null && getActivity() instanceof TAPBaseActivity) {
            return ((TAPBaseActivity)getActivity()).instanceKey;
        }
        return null;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public void onBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private TapCoreChatRoomListener chatRoomListener = new TapCoreChatRoomListener() {
        @Override
        public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {
            if (null != getRoom() && null != room && getRoom().getRoomID().equals(room.getRoomID())) {
                setRoom(room);
                TapBaseChatRoomCustomNavigationBarFragment.this.onReceiveUpdatedChatRoomData(room, recipientUser);
                Log.e(">>>>>>", "onReceiveUpdatedChatRoomData: " + TAPUtils.toJsonString(room));
            }
        }

        @Override
        public void onReceiveStartTyping(String roomID, TAPUserModel user) {
            if (null != getRoom() && getRoom().getRoomID().equals(roomID)) {
                TapBaseChatRoomCustomNavigationBarFragment.this.onReceiveStartTyping(roomID, user);
            }
        }

        @Override
        public void onReceiveStopTyping(String roomID, TAPUserModel user) {
            if (null != getRoom() && getRoom().getRoomID().equals(roomID)) {
                TapBaseChatRoomCustomNavigationBarFragment.this.onReceiveStopTyping(roomID, user);
            }
        }

        @Override
        public void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive) {
            if (null == getRoom() || null == user) {
                Log.e(">>>>>", "onReceiveOnlineStatus: RETURN");
                return;
            }
            Log.e(">>>>>", "onReceiveOnlineStatus userID: " + user.getUserID());
            Log.e(">>>>>", "onReceiveOnlineStatus: " + isOnline);
            boolean isRoomParticipant = false;
            if (getRoom().getType() == TYPE_PERSONAL) {
                String otherUserID = TAPChatManager.getInstance(getInstanceKey()).getOtherUserIdFromRoom(getRoom().getRoomID());
                Log.e(">>>>>", "onReceiveOnlineStatus otherUserID: " + otherUserID);
                if (user.getUserID().equals(otherUserID)) {
                    Log.e(">>>>>", "onReceiveOnlineStatus: isRoomParticipant");
                    isRoomParticipant = true;
                }
            }
            else if (null != getRoom().getParticipants()) {
                for (TAPUserModel participant : getRoom().getParticipants()) {
                    Log.e(">>>>>", "onReceiveOnlineStatus participantID: " + participant.getUserID());
                    if (user.getUserID().equals(participant.getUserID())) {
                        Log.e(">>>>>", "onReceiveOnlineStatus: isRoomParticipant");
                        isRoomParticipant = true;
                        break;
                    }
                }
            }

            if (isRoomParticipant) {
                Log.e(">>>>>", "onReceiveOnlineStatus: TRIGGER onReceiveOnlineStatus");
                TapBaseChatRoomCustomNavigationBarFragment.this.onReceiveOnlineStatus(user, isOnline, lastActive);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_cell_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TapCoreChatRoomManager.getInstance(getInstanceKey()).addChatRoomListener(chatRoomListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TapCoreChatRoomManager.getInstance(getInstanceKey()).removeChatRoomListener(chatRoomListener);
    }

    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {

    }

    public void onReceiveStartTyping(String roomID, TAPUserModel user) {

    }

    public void onReceiveStopTyping(String roomID, TAPUserModel user) {

    }

    public void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive) {

    }
}

package io.moselo.SampleApps.Fragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalkSample.R;

public class MeetTalkChatRoomNavigationBarFragment extends TapBaseChatRoomCustomNavigationBarFragment {

    private ConstraintLayout clRoomStatus;
    private CircleImageView civRoomImage;
    private ImageView ivButtonBack;
    private ImageView ivRoomIcon;
    private TextView tvRoomName;
    private TextView tvRoomStatus;
    private TextView tvRoomImageLabel;
    private View vRoomImage;

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

    private void setupNavigationData() {
        // Set room name
        if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            tvRoomName.setText(io.taptalk.TapTalk.R.string.tap_saved_messages);
        } else if (vm.getRoom().getType() == TYPE_PERSONAL && null != vm.getOtherUserModel() &&
                (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                !vm.getOtherUserModel().getFullname().isEmpty()) {
            tvRoomName.setText(vm.getOtherUserModel().getFullname());
        } else {
            tvRoomName.setText(vm.getRoom().getName());
        }

        if (!TapUI.getInstance(instanceKey).isProfileButtonVisible()) {
            civRoomImage.setVisibility(View.GONE);
            vRoomImage.setVisibility(View.GONE);
            tvRoomImageLabel.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() && null != vm.getOtherUserModel() &&
                (null != vm.getOtherUserModel().getDeleted() && vm.getOtherUserModel().getDeleted() > 0L)) {
            glide.load(io.taptalk.TapTalk.R.drawable.tap_ic_deleted_user).fitCenter().into(civRoomImage);
            ImageViewCompat.setImageTintList(civRoomImage, null);
            tvRoomImageLabel.setVisibility(View.GONE);
            clRoomStatus.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() && TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            glide.load(io.taptalk.TapTalk.R.drawable.tap_ic_bookmark_round).fitCenter().into(civRoomImage);
            ImageViewCompat.setImageTintList(civRoomImage, null);
            tvRoomImageLabel.setVisibility(View.GONE);
            clRoomStatus.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() && null != vm.getOtherUserModel() &&
                null != vm.getOtherUserModel().getImageURL().getThumbnail() &&
                !vm.getOtherUserModel().getImageURL().getThumbnail().isEmpty()) {
            // Load user avatar URL
            loadProfilePicture(vm.getOtherUserModel().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
            vm.getRoom().setImageURL(vm.getOtherUserModel().getImageURL());
        } else if (null != vm.getRoom() && !vm.getRoom().isDeleted() && null != vm.getRoom().getImageURL() && !vm.getRoom().getImageURL().getThumbnail().isEmpty()) {
            // Load room image
            loadProfilePicture(vm.getRoom().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
        } else {
            loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getUserRole() &&
                null != vm.getOtherUserModel().getUserRole().getIconURL() && null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() &&
                !vm.getOtherUserModel().getUserRole().getIconURL().isEmpty()) {
            glide.load(vm.getOtherUserModel().getUserRole().getIconURL()).into(ivRoomIcon);
            ivRoomIcon.setVisibility(View.VISIBLE);
        } else {
            ivRoomIcon.setVisibility(View.GONE);
        }

//        // Set typing status
//        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
//            vm.setOtherUserTyping(true);
//            showTypingIndicator();
//        }

        if (0 < vm.getGroupTypingSize()) {
            showTypingIndicator();
        }
    }
}

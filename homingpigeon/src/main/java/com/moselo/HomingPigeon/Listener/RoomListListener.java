package com.moselo.HomingPigeon.Listener;

import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Model.MessageModel;

public interface RoomListListener {

    void onRoomSelected(@Nullable MessageModel messageModel, boolean isSelected);
}

package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.MessageModel;

public interface RoomListListener {

    void onRoomSelected(MessageModel messageModel, boolean isSelected);
}

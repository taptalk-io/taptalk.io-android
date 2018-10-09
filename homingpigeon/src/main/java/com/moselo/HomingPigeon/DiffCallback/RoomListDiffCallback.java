package com.moselo.HomingPigeon.DiffCallback;

import android.support.v7.util.DiffUtil;

import com.moselo.HomingPigeon.Model.HpRoomListModel;

import java.util.List;

public class RoomListDiffCallback extends DiffUtil.Callback {
    private final List<HpRoomListModel> oldRoomList;
    private final List<HpRoomListModel> newRoomList;

    public RoomListDiffCallback(List<HpRoomListModel> oldRoomList, List<HpRoomListModel> newRoomList) {
        this.oldRoomList = oldRoomList;
        this.newRoomList = newRoomList;
    }

    @Override
    public int getOldListSize() {
        return oldRoomList.size();
    }

    @Override
    public int getNewListSize() {
        return newRoomList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRoomList.get(oldItemPosition).getLastMessage().getLocalID()
                .equals(newRoomList.get(newItemPosition).getLastMessage().getLocalID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRoomList.get(oldItemPosition).getLastMessage().getRoom().getRoomID()
                .equals(newRoomList.get(newItemPosition).getLastMessage().getRoom().getRoomID());
    }
}

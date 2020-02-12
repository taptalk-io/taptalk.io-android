package io.taptalk.TapTalk.DiffCallback;

import android.support.v7.util.DiffUtil;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPRoomListModel;

public class TAPRoomListDiffCallback extends DiffUtil.Callback {
    private final List<TAPRoomListModel> oldRoomList;
    private final List<TAPRoomListModel> newRoomList;

    public TAPRoomListDiffCallback(List<TAPRoomListModel> oldRoomList, List<TAPRoomListModel> newRoomList) {
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
        TAPRoomListModel oldRoom = oldRoomList.get(oldItemPosition);
        TAPRoomListModel newRoom = newRoomList.get(newItemPosition);
        if (null != oldRoom && null != newRoom) {
            return oldRoom.getLastMessage().getRoom().getRoomID()
                    .equals(newRoom.getLastMessage().getRoom().getRoomID());
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TAPRoomListModel oldRoom = oldRoomList.get(oldItemPosition);
        TAPRoomListModel newRoom = newRoomList.get(newItemPosition);
        if (null != oldRoom && null != newRoom) {
            return oldRoom.getLastMessage().getLocalID()
                    .equals(newRoom.getLastMessage().getLocalID());
        }
        return false;
    }
}

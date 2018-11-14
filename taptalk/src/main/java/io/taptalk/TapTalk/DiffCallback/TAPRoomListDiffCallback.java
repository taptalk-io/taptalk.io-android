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
        return oldRoomList.get(oldItemPosition).getLastMessage().getRoom().getRoomID()
                .equals(newRoomList.get(newItemPosition).getLastMessage().getRoom().getRoomID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRoomList.get(oldItemPosition).getLastMessage().getLocalID()
                .equals(newRoomList.get(newItemPosition).getLastMessage().getLocalID());
    }
}

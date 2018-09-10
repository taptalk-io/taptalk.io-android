package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Model.MessageModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoomListViewModel extends AndroidViewModel {

    private List<MessageModel> roomList;
    private Map<String, MessageModel> selectedRooms;
    private boolean isSelecting;

    public RoomListViewModel(@NonNull Application application) {
        super(application);
        roomList = new ArrayList<>();
        selectedRooms = new LinkedHashMap<>();
    }

    public List<MessageModel> getRoomList() {
        return roomList;
    }

    public Map<String, MessageModel> getSelectedRooms() {
        return selectedRooms;
    }

    public void setSelectedRooms(Map<String, MessageModel> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public int getSelectedCount() {
        return selectedRooms.size();
    }

    public void setRoomList(List<MessageModel> roomList) {
        this.roomList = roomList;
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }
}

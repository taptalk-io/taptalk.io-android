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
    private Map<String, MessageModel> roomPointer;
    private Map<String, MessageModel> selectedRooms;
    private boolean isSelecting;

    public RoomListViewModel(@NonNull Application application) {
        super(application);
    }

    public List<MessageModel> getRoomList() {
        return roomList == null ? roomList = new ArrayList<>() : roomList;
    }

    public void setRoomList(List<MessageModel> roomList) {
        this.roomList = roomList;
    }

    public void addRoomList(List<MessageModel> roomList) {
        getRoomList().addAll(roomList);
    }

    public void clearRoomList() {
        getRoomList().clear();
    }

    public Map<String, MessageModel> getRoomPointer() {
        return roomPointer == null ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public void setRoomPointer(Map<String, MessageModel> roomPointer) {
        this.roomPointer = roomPointer;
    }

    public void addRoomPointer(MessageModel messageModel){
        getRoomPointer().put(messageModel.getRoom().getRoomID(), messageModel);
    }

    public Map<String, MessageModel> getSelectedRooms() {
        return selectedRooms == null ? selectedRooms = new LinkedHashMap<>() : selectedRooms;
    }

    public void setSelectedRooms(Map<String, MessageModel> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public int getSelectedCount() {
        return selectedRooms.size();
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }
}

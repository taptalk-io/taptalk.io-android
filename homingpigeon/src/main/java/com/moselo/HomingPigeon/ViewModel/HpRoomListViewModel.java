package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomListModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpRoomListViewModel extends AndroidViewModel {
    private List<RoomListModel> roomList;
    private Map<String, RoomListModel> roomPointer;
    private Map<String, RoomListModel> selectedRooms;
    private boolean isSelecting;
    private boolean isFirstTime;
    private String myUserID;

    public HpRoomListViewModel(@NonNull Application application) {
        super(application);
        myUserID = HpDataManager.getInstance().getActiveUser(application).getUserID();
    }

    public List<RoomListModel> getRoomList() {
        return roomList == null ? roomList = new ArrayList<>() : roomList;
    }

    public void setRoomList(List<RoomListModel> roomList) {
        this.roomList = roomList;
    }

    public void addRoomList(List<RoomListModel> roomList) {
        getRoomList().addAll(roomList);
    }

    public void clearRoomList() {
        getRoomList().clear();
    }

    public Map<String, RoomListModel> getRoomPointer() {
        return roomPointer == null ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public void setRoomPointer(Map<String, RoomListModel> roomPointer) {
        this.roomPointer = roomPointer;
    }

    public void addRoomPointer(RoomListModel roomModel){
        getRoomPointer().put(roomModel.getLastMessage().getRoom().getRoomID(), roomModel);
    }

    public Map<String, RoomListModel> getSelectedRooms() {
        return selectedRooms == null ? selectedRooms = new LinkedHashMap<>() : selectedRooms;
    }

    public void setSelectedRooms(Map<String, RoomListModel> selectedRooms) {
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

    public String getMyUserID() {
        return myUserID;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }
}

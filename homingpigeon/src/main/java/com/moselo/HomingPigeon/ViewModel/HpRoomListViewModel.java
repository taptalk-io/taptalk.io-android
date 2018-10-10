package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpRoomListModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpRoomListViewModel extends AndroidViewModel {
    private List<HpRoomListModel> roomList;
    private Map<String, HpRoomListModel> roomPointer;
    private Map<String, HpRoomListModel> selectedRooms;
    private boolean isSelecting;
    private static boolean isShouldNotLoadFromAPI = false;
    private boolean isDoneFirstSetup = false;
    private boolean isApiCalled = false;
    private String myUserID;

    public HpRoomListViewModel(@NonNull Application application) {
        super(application);
        myUserID = HpDataManager.getInstance().getActiveUser(application).getUserID();
    }

    public List<HpRoomListModel> getRoomList() {
        return roomList == null ? roomList = new ArrayList<>() : roomList;
    }

    public void setRoomList(List<HpRoomListModel> roomList) {
        this.roomList = roomList;
    }

    public void addRoomList(List<HpRoomListModel> roomList) {
        getRoomList().addAll(roomList);
    }

    public void clearRoomList() {
        getRoomList().clear();
    }

    public Map<String, HpRoomListModel> getRoomPointer() {
        return roomPointer == null ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public void setRoomPointer(Map<String, HpRoomListModel> roomPointer) {
        this.roomPointer = roomPointer;
    }

    public void addRoomPointer(HpRoomListModel roomModel){
        getRoomPointer().put(roomModel.getLastMessage().getRoom().getRoomID(), roomModel);
    }

    public Map<String, HpRoomListModel> getSelectedRooms() {
        return selectedRooms == null ? selectedRooms = new LinkedHashMap<>() : selectedRooms;
    }

    public void setSelectedRooms(Map<String, HpRoomListModel> selectedRooms) {
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

    public static boolean isShouldNotLoadFromAPI() {
        return isShouldNotLoadFromAPI;
    }

    public static void setShouldNotLoadFromAPI(boolean shouldNotLoadFromAPI) {
        isShouldNotLoadFromAPI = shouldNotLoadFromAPI;
    }

    public boolean isDoneFirstSetup() {
        return isDoneFirstSetup;
    }

    public void setDoneFirstSetup(boolean doneFirstSetup) {
        isDoneFirstSetup = doneFirstSetup;
    }

    public boolean isApiCalled() {
        return isApiCalled;
    }

    public void setApiCalled(boolean apiCalled) {
        isApiCalled = apiCalled;
    }
}

package io.taptalk.TapTalk.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPRoomListModel;

public class TAPRoomListViewModel extends AndroidViewModel {
    private String instanceKey = "";
    private List<TAPRoomListModel> roomList;
    private Map<String, TAPRoomListModel> roomPointer;
    private Map<String, TAPRoomListModel> selectedRooms;
    private String myUserID;
    private int roomBadgeCount;
    private int lastBadgeCount;
    private boolean isSelecting;
    private boolean isDoneFirstSetup = false;
    private boolean isDoneFirstApiSetup = false;
    private static boolean isShouldNotLoadFromAPI = false;

    public static class TAPRoomListViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String instanceKey;

        public TAPRoomListViewModelFactory(Application application, String instanceKey) {
            this.application = application;
            this.instanceKey = instanceKey;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TAPRoomListViewModel(application, instanceKey);
        }
    }

    public TAPRoomListViewModel(@NonNull Application application, String instanceKey) {
        super(application);
        this.instanceKey = instanceKey;
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public List<TAPRoomListModel> getRoomList() {
        return roomList == null ? roomList = new ArrayList<>() : roomList;
    }

    public void setRoomList(List<TAPRoomListModel> roomList) {
        this.roomList = roomList;
    }

    public void addRoomList(List<TAPRoomListModel> roomList) {
        getRoomList().addAll(roomList);
    }

    public void clearRoomList() {
        getRoomList().clear();
    }

    public Map<String, TAPRoomListModel> getRoomPointer() {
        return roomPointer == null ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public void setRoomPointer(Map<String, TAPRoomListModel> roomPointer) {
        this.roomPointer = roomPointer;
    }

    public void addRoomPointer(TAPRoomListModel roomModel) {
        getRoomPointer().put(roomModel.getLastMessage().getRoom().getRoomID(), roomModel);
    }

    public Map<String, TAPRoomListModel> getSelectedRooms() {
        return selectedRooms == null ? selectedRooms = new LinkedHashMap<>() : selectedRooms;
    }

    public void setSelectedRooms(Map<String, TAPRoomListModel> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public int getSelectedCount() {
        return getSelectedRooms().size();
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }

    public String getMyUserID() {
        return null == myUserID ?
                null != TAPChatManager.getInstance(instanceKey).getActiveUser() ?
                        myUserID = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID() :
                        null
                : myUserID;
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

    public boolean isDoneFirstApiSetup() {
        return isDoneFirstApiSetup;
    }

    public void setDoneFirstApiSetup(boolean doneFirstApiSetup) {
        isDoneFirstApiSetup = doneFirstApiSetup;
    }

    public int getRoomBadgeCount() {
        return roomBadgeCount;
    }

    public void setRoomBadgeCount(int roomBadgeCount) {
        this.roomBadgeCount = roomBadgeCount;
    }

    public int getLastBadgeCount() {
        return lastBadgeCount;
    }

    public void setLastBadgeCount(int lastBadgeCount) {
        this.lastBadgeCount = lastBadgeCount;
    }
}

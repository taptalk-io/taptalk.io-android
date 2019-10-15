package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room;
    private List<TapChatProfileItemModel> menuItems;
    private List<TapChatProfileItemModel> sharedMediaItems;
    private List<TapChatProfileItemModel> adapterItems;
    private TapChatProfileItemModel sharedMediaSectionTitle;
    private TapChatProfileItemModel loadingItem;
    private List<TAPMessageModel> sharedMedias;
    private HashMap<String, TAPMessageModel> sharedMediasMap;
    private TAPMessageModel pendingDownloadMessage;
    private TAPUserModel groupMemberUser;
    private long lastSharedMediaTimestamp;
    private boolean isLoadingSharedMedia, isFinishedLoadingSharedMedia, isAdminGroup;

    public TAPProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPRoomModel getRoom() {
        return null == room ? room = new TAPRoomModel() : room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public List<TapChatProfileItemModel> getMenuItems() {
        return null == menuItems ? menuItems = new ArrayList<>() : menuItems;
    }

    public void setMenuItems(List<TapChatProfileItemModel> menuItems) {
        this.menuItems = menuItems;
    }

    public List<TapChatProfileItemModel> getSharedMediaItems() {
        return null == sharedMediaItems ? sharedMediaItems = new ArrayList<>() : sharedMediaItems;
    }

    public List<TapChatProfileItemModel> getAdapterItems() {
        return null == adapterItems ? adapterItems = new ArrayList<>() : adapterItems;
    }

    public void setAdapterItems(List<TapChatProfileItemModel> adapterItems) {
        this.adapterItems = adapterItems;
    }

    public TapChatProfileItemModel getSharedMediaSectionTitle() {
        return sharedMediaSectionTitle;
    }

    public void setSharedMediaSectionTitle(TapChatProfileItemModel sharedMediaSectionTitle) {
        this.sharedMediaSectionTitle = sharedMediaSectionTitle;
    }

    public TapChatProfileItemModel getLoadingItem() {
        return loadingItem;
    }

    public void setLoadingItem(TapChatProfileItemModel loadingItem) {
        this.loadingItem = loadingItem;
    }

    public List<TAPMessageModel> getSharedMedias() {
        return null == sharedMedias ? sharedMedias = new ArrayList<>() : sharedMedias;
    }

    private HashMap<String, TAPMessageModel> getSharedMediasMap() {
        return null == sharedMediasMap ? sharedMediasMap = new HashMap<>() : sharedMediasMap;
    }

    public void addSharedMedia(TAPMessageModel sharedMedia) {
        getSharedMedias().add(sharedMedia);
        getSharedMediasMap().put(sharedMedia.getLocalID(), sharedMedia);
        getSharedMediaItems().add(new TapChatProfileItemModel(sharedMedia));
    }

    public TAPMessageModel getSharedMedia(String localID) {
        return getSharedMediasMap().get(localID);
    }

    public TAPMessageModel getPendingDownloadMessage() {
        return pendingDownloadMessage;
    }

    public void setPendingDownloadMessage(TAPMessageModel pendingDownloadMessage) {
        this.pendingDownloadMessage = pendingDownloadMessage;
    }

    public long getLastSharedMediaTimestamp() {
        return lastSharedMediaTimestamp;
    }

    public void setLastSharedMediaTimestamp(long lastSharedMediaTimestamp) {
        this.lastSharedMediaTimestamp = lastSharedMediaTimestamp;
    }

    public boolean isLoadingSharedMedia() {
        return isLoadingSharedMedia;
    }

    public void setLoadingSharedMedia(boolean loadingSharedMedia) {
        isLoadingSharedMedia = loadingSharedMedia;
    }

    public boolean isFinishedLoadingSharedMedia() {
        return isFinishedLoadingSharedMedia;
    }

    public void setFinishedLoadingSharedMedia(boolean finishedLoadingSharedMedia) {
        isFinishedLoadingSharedMedia = finishedLoadingSharedMedia;
    }

    public TAPUserModel getGroupMemberUser() {
        return groupMemberUser;
    }

    public void setGroupMemberUser(TAPUserModel groupMemberUser) {
        this.groupMemberUser = groupMemberUser;
    }

    public boolean isAdminGroup() {
        return isAdminGroup;
    }

    public void setAdminGroup(boolean adminGroup) {
        isAdminGroup = adminGroup;
    }
}

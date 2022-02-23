package io.taptalk.TapTalk.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPProfileViewModel extends AndroidViewModel {

    private TAPRoomModel room, groupDataFromManager;
    private List<TapChatProfileItemModel> menuItems;
    private List<TapChatProfileItemModel> adapterItems;
    private List<TapChatProfileItemModel> sharedMediaAdapterItems;
    private TapChatProfileItemModel profileDetailItem;
    private TapChatProfileItemModel sharedMediaSectionTitle;
    private TapChatProfileItemModel loadingItem;
    private List<TAPMessageModel> sharedMedias;
    private HashMap<String, TAPMessageModel> sharedMediasMap;
    private TAPMessageModel pendingDownloadMessage;
    private TAPUserModel groupMemberUser, userDataFromManager;
    private String loadingStartText, loadingEndText;
    private long lastSharedMediaTimestamp;
    private boolean isLoadingSharedMedia, isFinishedLoadingSharedMedia, isApiCallOnProgress, isGroupMemberProfile, isGroupAdmin;
    private ArrayList<String> profilePictureUriList;

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

    public List<TapChatProfileItemModel> getAdapterItems() {
        return null == adapterItems ? adapterItems = new ArrayList<>() : adapterItems;
    }

    public void setAdapterItems(List<TapChatProfileItemModel> adapterItems) {
        this.adapterItems = adapterItems;
    }

    public List<TapChatProfileItemModel> getSharedMediaAdapterItems() {
        return null == sharedMediaAdapterItems ? sharedMediaAdapterItems = new ArrayList<>() : sharedMediaAdapterItems;
    }

    public void setSharedMediaAdapterItems(List<TapChatProfileItemModel> sharedMediaAdapterItems) {
        this.sharedMediaAdapterItems = sharedMediaAdapterItems;
    }

    public TapChatProfileItemModel getProfileDetailItem() {
        return profileDetailItem;
    }

    public void setProfileDetailItem(TapChatProfileItemModel profileDetailItem) {
        this.profileDetailItem = profileDetailItem;
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

    public String getLoadingStartText() {
        return loadingStartText;
    }

    public void setLoadingStartText(String loadingStartText) {
        this.loadingStartText = loadingStartText;
    }

    public String getLoadingEndText() {
        return loadingEndText;
    }

    public void setLoadingEndText(String loadingEndText) {
        this.loadingEndText = loadingEndText;
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

    public TAPRoomModel getGroupDataFromManager() {
        return groupDataFromManager;
    }

    public void setGroupDataFromManager(TAPRoomModel groupDataFromManager) {
        this.groupDataFromManager = groupDataFromManager;
    }

    public TAPUserModel getUserDataFromManager() {
        return userDataFromManager;
    }

    public void setUserDataFromManager(TAPUserModel userDataFromManager) {
        this.userDataFromManager = userDataFromManager;
    }

    public boolean isApiCallOnProgress() {
        return isApiCallOnProgress;
    }

    public void setApiCallOnProgress(boolean apiCallOnProgress) {
        isApiCallOnProgress = apiCallOnProgress;
    }

    public boolean isGroupMemberProfile() {
        return isGroupMemberProfile;
    }

    public void setGroupMemberProfile(boolean groupMemberProfile) {
        isGroupMemberProfile = groupMemberProfile;
    }

    public boolean isGroupAdmin() {
        return isGroupAdmin;
    }

    public void setGroupAdmin(boolean isGroupAdmin) {
        this.isGroupAdmin = isGroupAdmin;
    }

    public ArrayList<String> getProfilePictureUriList() {
        return profilePictureUriList == null? profilePictureUriList = new ArrayList<>() : profilePictureUriList;
    }

    public void setProfilePictureUriList(ArrayList<String> profilePictureUriList) {
        this.profilePictureUriList = profilePictureUriList;
    }
}

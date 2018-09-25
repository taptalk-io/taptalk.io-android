package com.moselo.HomingPigeon.Model;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;

public class SearchChatModel {
    public enum MyReturnType {
        RECENT_TITLE, RECENT_ITEM,
        SECTION_TITLE, CHAT_ITEM,
        MESSAGE_ITEM, CONTACT_ITEM,
        EMPTY_STATE
    }

    private MyReturnType myReturnType;
    private RecentSearchEntity recentSearch;
    private String sectionTitle;
    private RoomModel room;
    private MessageEntity message;
    private UserModel contact;
    private boolean isLastInSection = false;

    public SearchChatModel(MyReturnType myReturnType) {
        this.myReturnType = myReturnType;
    }

    public MyReturnType getMyReturnType() {
        return myReturnType;
    }

    public void setMyReturnType(MyReturnType myReturnType) {
        this.myReturnType = myReturnType;
    }

    public RecentSearchEntity getRecentSearch() {
        return recentSearch;
    }

    public void setRecentSearch(RecentSearchEntity recentSearch) {
        this.recentSearch = recentSearch;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }

    public UserModel getContact() {
        return contact;
    }

    public void setContact(UserModel contact) {
        this.contact = contact;
    }

    public boolean isLastInSection() {
        return isLastInSection;
    }

    public void setLastInSection(boolean lastInSection) {
        isLastInSection = lastInSection;
    }
}

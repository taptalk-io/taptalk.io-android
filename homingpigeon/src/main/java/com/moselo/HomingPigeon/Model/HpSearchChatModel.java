package com.moselo.HomingPigeon.Model;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;

public class HpSearchChatModel {
    public enum Type {
        RECENT_TITLE, RECENT_ITEM,
        SECTION_TITLE, CHAT_ITEM,
        MESSAGE_ITEM, ROOM_ITEM,
        CONTACT_ITEM, EMPTY_STATE
    }

    private Type type;
    private HpRecentSearchEntity recentSearch;
    private String sectionTitle;
    private HpRoomModel room;
    private HpMessageEntity message;
    private HpUserModel contact;
    private boolean isLastInSection = false;

    public HpSearchChatModel(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public HpRecentSearchEntity getRecentSearch() {
        return recentSearch;
    }

    public void setRecentSearch(HpRecentSearchEntity recentSearch) {
        this.recentSearch = recentSearch;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public HpRoomModel getRoom() {
        return room;
    }

    public void setRoom(HpRoomModel room) {
        this.room = room;
    }

    public HpMessageEntity getMessage() {
        return message;
    }

    public void setMessage(HpMessageEntity message) {
        this.message = message;
    }

    public HpUserModel getContact() {
        return contact;
    }

    public void setContact(HpUserModel contact) {
        this.contact = contact;
    }

    public boolean isLastInSection() {
        return isLastInSection;
    }

    public void setLastInSection(boolean lastInSection) {
        isLastInSection = lastInSection;
    }
}

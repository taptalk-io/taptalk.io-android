package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPSendCustomMessageRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("recipientUserID") private String recipientUserID;
    @JsonProperty("localID") private String localID;
    @JsonProperty("messageType") private Integer messageType;
    @JsonProperty("body") private String body;
    @JsonProperty("data") private String data;
    @JsonProperty("filterID") private String filterID;
    @JsonProperty("isHidden") private Boolean isHidden;

    public TAPSendCustomMessageRequest(String roomID, String recipientUserID, String localID, Integer messageType, String body, String data, String filterID, Boolean isHidden) {
        this.roomID = roomID;
        this.recipientUserID = recipientUserID;
        this.localID = localID;
        this.messageType = messageType;
        this.body = body;
        this.data = data;
        this.filterID = filterID;
        this.isHidden = isHidden;
    }

    public TAPSendCustomMessageRequest() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRecipientUserID() {
        return recipientUserID;
    }

    public void setRecipientUserID(String recipientUserID) {
        this.recipientUserID = recipientUserID;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(String filterID) {
        this.filterID = filterID;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }
}

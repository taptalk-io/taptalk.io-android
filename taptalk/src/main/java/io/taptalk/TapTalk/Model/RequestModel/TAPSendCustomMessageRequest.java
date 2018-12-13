package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPSendCustomMessageRequest {
    @JsonProperty("messageType") private Integer messageType;
    @JsonProperty("body") private String body;
    @JsonProperty("filterID") private String filterID;
    @JsonProperty("senderUserID") private String senderUserID;
    @JsonProperty("recipientUserID") private String recipientUserID;

    public TAPSendCustomMessageRequest(Integer messageType, String body, String filterID, String senderUserID, String recipientUserID) {
        this.messageType = messageType;
        this.body = body;
        this.filterID = filterID;
        this.senderUserID = senderUserID;
        this.recipientUserID = recipientUserID;
    }

    public TAPSendCustomMessageRequest() {
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

    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(String filterID) {
        this.filterID = filterID;
    }

    public String getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        this.senderUserID = senderUserID;
    }

    public String getRecipientUserID() {
        return recipientUserID;
    }

    public void setRecipientUserID(String recipientUserID) {
        this.recipientUserID = recipientUserID;
    }
}

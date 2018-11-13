package com.moselo.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.TAPContactModel;

import java.util.List;

public class TAPContactResponse {
    @JsonProperty("contacts") private List<TAPContactModel> contacts;

    public TAPContactResponse() {}

    public List<TAPContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<TAPContactModel> contacts) {
        this.contacts = contacts;
    }
}

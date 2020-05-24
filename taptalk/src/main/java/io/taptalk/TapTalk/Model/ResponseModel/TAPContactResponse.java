package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPContactModel;

public class TAPContactResponse {
    @JsonProperty("contacts") private List<TAPContactModel> contacts;

    public TAPContactResponse() {
    }

    public List<TAPContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<TAPContactModel> contacts) {
        this.contacts = contacts;
    }
}

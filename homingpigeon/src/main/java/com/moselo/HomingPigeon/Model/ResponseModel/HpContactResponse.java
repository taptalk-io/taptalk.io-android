package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Model.HpContactModel;

import java.util.List;

public class HpContactResponse {
    @JsonProperty("contacts") private List<HpContactModel> contacts;

    public HpContactResponse() {}

    public List<HpContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<HpContactModel> contacts) {
        this.contacts = contacts;
    }
}

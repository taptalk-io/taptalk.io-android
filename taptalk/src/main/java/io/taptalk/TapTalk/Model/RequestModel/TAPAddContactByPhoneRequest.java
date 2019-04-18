package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPAddContactByPhoneRequest {
    @JsonProperty("phones") private List<String> phones;

    public TAPAddContactByPhoneRequest(List<String> phones) {
        this.phones = phones;
    }

    public TAPAddContactByPhoneRequest() {
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}

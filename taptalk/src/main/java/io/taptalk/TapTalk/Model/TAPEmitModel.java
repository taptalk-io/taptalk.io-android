package io.taptalk.TapTalk.Model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPEmitModel<T> {

    @JsonProperty("eventName")
    private String eventName;
    @JsonProperty("data")
    private T data;

    public TAPEmitModel(String eventName, T data) {
        this.eventName = eventName;
        this.data = data;
    }

    public TAPEmitModel() {
    }

    @JsonProperty("eventName")
    public String getEventName() {
        return eventName;
    }

    @JsonProperty("eventName")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(T data) {
        this.data = data;
    }
}

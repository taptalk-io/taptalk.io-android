package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.taptalk.TapTalk.API.deserializer.TAPErrorEmptyAsNullDeserializer;
import io.taptalk.TapTalk.Model.TAPErrorModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TAPBaseResponse<T> {
    @JsonDeserialize(using = TAPErrorEmptyAsNullDeserializer.class)
    @JsonProperty("error")
    private TAPErrorModel error;

    @JsonProperty("data") private T data;

    @JsonProperty("status") private int status;

    public TAPErrorModel getError() {
        return error;
    }

    public void setError(TAPErrorModel error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

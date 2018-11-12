package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moselo.HomingPigeon.API.deserializer.ErrorEmptyAsNullDeserializer;
import com.moselo.HomingPigeon.Model.HpErrorModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class BaseResponse<T> {
    @JsonDeserialize(using = ErrorEmptyAsNullDeserializer.class)
    @JsonProperty("error")
    private HpErrorModel error;

    @JsonProperty("data") private T data;

    @JsonProperty("status") private int status;

    public HpErrorModel getError() {
        return error;
    }

    public void setError(HpErrorModel error) {
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

package com.moselo.HomingPigeon.API;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moselo.HomingPigeon.API.deserializer.ErrorEmptyAsNullDeserializer;
import com.moselo.HomingPigeon.Model.ErrorModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class BaseResponse<T> {
    @JsonDeserialize(using = ErrorEmptyAsNullDeserializer.class)
    @JsonProperty("error")
    private ErrorModel error;

    @JsonProperty("data") private T data;

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

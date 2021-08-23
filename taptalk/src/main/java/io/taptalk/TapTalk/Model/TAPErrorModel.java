package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TAPErrorModel {
    @JsonProperty("code") private String code;
    @JsonProperty("message") private String message;
    @JsonProperty("field") private String field;

    public TAPErrorModel() {
    }

    public TAPErrorModel(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public static TAPErrorModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPErrorModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

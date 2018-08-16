package com.moselo.HomingPigeon.Helper;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

public class Utils {

    private static Utils instance;
    private ObjectMapper objectMapper;

    public static Utils getInstance() {

        if (null == instance)
            instance = new Utils();

        return instance;
    }


    public Utils() {
        objectMapper = new ObjectMapper();
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(Utils.class.getSimpleName(), "toJsonString: ",e );
            return "{}";
        }
    }

    public JSONObject toJsonObject(Object object) {
        try {
            return new JSONObject(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        try {
            return objectMapper.readValue(jsonPacket, type);
        } catch (Exception e) {
            Log.e(Utils.class.getSimpleName(), "fromJSON: ",e );
            return null;
        }
    }
}

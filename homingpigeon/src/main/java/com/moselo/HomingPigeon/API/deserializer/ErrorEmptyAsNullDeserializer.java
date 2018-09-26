package com.moselo.HomingPigeon.API.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.moselo.HomingPigeon.Model.ErrorModel;

import java.io.IOException;

/**
 * Created by Fadhlan on 4/18/17.
 */

public class ErrorEmptyAsNullDeserializer extends JsonDeserializer<ErrorModel> {

    private static final String LOG_TAG = ErrorEmptyAsNullDeserializer.class.getSimpleName();

    @Override
    public ErrorModel deserialize(JsonParser p,
                                  DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (node.size() == 0) {
            return null;
        }
        ErrorModel error = new ErrorModel();
        error.setCode(node.get("code").asText());
        error.setMessage(node.get("message").asText());
        return error;
    }
}

package com.moselo.HomingPigeon.API.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.moselo.HomingPigeon.Model.HpErrorModel;

import java.io.IOException;

/**
 * Created by Fadhlan on 4/18/17.
 */

public class TAPErrorEmptyAsNullDeserializer extends JsonDeserializer<HpErrorModel> {

    private static final String LOG_TAG = TAPErrorEmptyAsNullDeserializer.class.getSimpleName();

    @Override
    public HpErrorModel deserialize(JsonParser p,
                                    DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (node.size() == 0) {
            return null;
        }
        HpErrorModel error = new HpErrorModel();
        error.setCode(node.get("code").asText());
        error.setMessage(node.get("message").asText());
        return error;
    }
}

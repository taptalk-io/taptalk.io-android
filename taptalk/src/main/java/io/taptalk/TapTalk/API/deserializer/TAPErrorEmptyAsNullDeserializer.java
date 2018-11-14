package io.taptalk.TapTalk.API.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import io.taptalk.TapTalk.Model.TAPErrorModel;

/**
 * Created by Fadhlan on 4/18/17.
 */

public class TAPErrorEmptyAsNullDeserializer extends JsonDeserializer<TAPErrorModel> {

    private static final String LOG_TAG = TAPErrorEmptyAsNullDeserializer.class.getSimpleName();

    @Override
    public TAPErrorModel deserialize(JsonParser p,
                                     DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (node.size() == 0) {
            return null;
        }
        TAPErrorModel error = new TAPErrorModel();
        error.setCode(node.get("code").asText());
        error.setMessage(node.get("message").asText());
        return error;
    }
}

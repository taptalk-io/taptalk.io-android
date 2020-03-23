package io.taptalk.TapTalk.Model;

import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TAPPairIdNameModel {
    @JsonProperty("id") private String id;
    @JsonProperty("name") private String name;

    @Ignore
    public TAPPairIdNameModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TAPPairIdNameModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TapConfig;

public class TAPProjectConfigResponse {

    @JsonProperty("configs")
    private TapConfig configs;

    public void setConfigs(TapConfig configs) {
        this.configs = configs;
    }

    public TapConfig getConfigs() {
        return configs;
    }
}
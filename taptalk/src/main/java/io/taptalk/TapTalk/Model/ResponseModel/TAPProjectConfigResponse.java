package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TapConfigs;

public class TAPProjectConfigResponse {

    @JsonProperty("configs")
    private TapConfigs configs;

    public void setConfigs(TapConfigs configs) {
        this.configs = configs;
    }

    public TapConfigs getConfigs() {
        return configs;
    }
}
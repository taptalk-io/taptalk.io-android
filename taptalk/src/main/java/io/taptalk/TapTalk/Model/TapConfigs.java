package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TapConfigs {

    @JsonProperty("core")
    private Map<String, String> coreConfigs;

    @JsonProperty("project")
    private Map<String, String> projectConfigs;

    @JsonProperty("custom")
    private Map<String, String> customConfigs;

    public Map<String, String> getCoreConfigs() {
        return coreConfigs;
    }

    public void setCoreConfigs(Map<String, String> coreConfigs) {
        this.coreConfigs = coreConfigs;
    }

    public Map<String, String> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(Map<String, String> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public Map<String, String> getCustomConfigs() {
        return customConfigs;
    }

    public void setCustomConfigs(Map<String, String> customConfigs) {
        this.customConfigs = customConfigs;
    }
}
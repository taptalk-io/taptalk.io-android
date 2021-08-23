package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TapConfigs {

    @JsonProperty("core")
    private Map<String, String> coreConfigs;

    @JsonProperty("project")
    private Map<String, String> projectConfigs;

    @JsonProperty("custom")
    private Map<String, String> customConfigs;

    public static TapConfigs fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TapConfigs>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

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
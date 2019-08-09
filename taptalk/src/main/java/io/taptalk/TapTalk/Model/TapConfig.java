package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TapConfig {

    @JsonProperty("core")
    private Map<String, String> core;

    @JsonProperty("project")
    private Map<String, String> project;

    @JsonProperty("custom")
    private Map<String, String> custom;

    public Map<String, String> getCore() {
        return core;
    }

    public void setCore(Map<String, String> core) {
        this.core = core;
    }

    public Map<String, String> getProject() {
        return project;
    }

    public void setProject(Map<String, String> project) {
        this.project = project;
    }

    public Map<String, String> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, String> custom) {
        this.custom = custom;
    }
}
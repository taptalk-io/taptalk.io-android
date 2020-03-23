package io.taptalk.TapTalk.Manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;

public class TAPCustomBubbleManager {

    private static HashMap<String, TAPCustomBubbleManager> instances;

    private Map<Integer, TAPBaseCustomBubble> customBubbleMap;

    private String instanceKey = "";

    public TAPCustomBubbleManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TAPCustomBubbleManager getInstance() {
        return getInstance("");
    }

    public static TAPCustomBubbleManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPCustomBubbleManager instance = new TAPCustomBubbleManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPCustomBubbleManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public Map<Integer, TAPBaseCustomBubble> getCustomBubbleMap() {
        return null == customBubbleMap ? customBubbleMap = new LinkedHashMap<>() : customBubbleMap;
    }

    public void addCustomBubbleMap(TAPBaseCustomBubble customBubble) {
        getCustomBubbleMap().put(customBubble.getMessageType(), customBubble);
    }
}

package io.taptalk.TapTalk.Manager;

import java.util.LinkedHashMap;
import java.util.Map;

import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;

public class TAPCustomBubbleManager {
    private static TAPCustomBubbleManager instance;
    private Map<Integer, TAPBaseCustomBubble> customBubbleMap;

    public static TAPCustomBubbleManager getInstance() {
        return null == instance ? instance = new TAPCustomBubbleManager() : instance;
    }

    public Map<Integer, TAPBaseCustomBubble> getCustomBubbleMap() {
        return null == customBubbleMap ? customBubbleMap = new LinkedHashMap<>() : customBubbleMap;
    }

    public void addCustomBubbleMap(TAPBaseCustomBubble customBubble) {
        getCustomBubbleMap().put(customBubble.getMessageType(), customBubble);
    }
}

package io.taptalk.TapTalk.Manager;

public class TAPCustomBubbleManager {
    private static TAPCustomBubbleManager instance;

    public static TAPCustomBubbleManager getInstance() {
        return null == instance ? instance = new TAPCustomBubbleManager() : instance;
    }
}

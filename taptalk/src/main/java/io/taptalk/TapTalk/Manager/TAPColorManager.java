package io.taptalk.TapTalk.Manager;

public class TAPColorManager {
    private static TAPColorManager instance;

    public static TAPColorManager getInstance() {
        return null == instance ? instance = new TAPColorManager() : instance;
    }
}

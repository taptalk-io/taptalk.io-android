package com.moselo.HomingPigeon.Manager;

public class HpMessageStatusManager {
    private static final String TAG = HpMessageStatusManager.class.getSimpleName();
    private static HpMessageStatusManager instance;

    public static HpMessageStatusManager getInstance() {
        return null == instance ? instance = new HpMessageStatusManager() : instance;
    }
}

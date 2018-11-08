package com.moselo.HomingPigeon.Manager;

public class HpOldDataManager {
    private static final String TAG = HpOldDataManager.class.getSimpleName();
    private static HpOldDataManager instance;

    public static HpOldDataManager getInstance() {
        return null == instance ? instance = new HpOldDataManager() : instance;
    }
}

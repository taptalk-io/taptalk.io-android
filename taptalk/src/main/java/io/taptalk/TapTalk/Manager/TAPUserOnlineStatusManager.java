package io.taptalk.TapTalk.Manager;

import java.util.HashMap;

@Deprecated
public class TAPUserOnlineStatusManager {

    private static TAPUserOnlineStatusManager instance;

    private HashMap<String, Long> userLastActivityMap;

    public TAPUserOnlineStatusManager() {
    }

    public static TAPUserOnlineStatusManager getInstance() {
        return null == instance ? instance = new TAPUserOnlineStatusManager() : instance;
    }

    public Long getUserLastActivity(String userID) {
        return getUserLastActivityMap().get(userID) == null ? 0L : getUserLastActivityMap().get(userID);
    }

    public void updateUserLastActivity(String userID, Long lastActive) {
        getUserLastActivityMap().put(userID, lastActive);
        saveUserLastActivityMapToPreference();
    }

    private HashMap<String, Long> getUserLastActivityMap() {
        return null == userLastActivityMap ?
                userLastActivityMap = null == TAPDataManager.getInstance().getUserLastActivityMap() ?
                        userLastActivityMap = new HashMap<>() :
                        TAPDataManager.getInstance().getUserLastActivityMap() :
                userLastActivityMap;
    }

    private void saveUserLastActivityMapToPreference() {
        TAPDataManager.getInstance().saveUserLastActivityMap(userLastActivityMap);
    }
}

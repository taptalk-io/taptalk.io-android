package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPContactManager {

    private static TAPContactManager instance;

    private HashMap<String, TAPUserModel> userDataMap;

    public static TAPContactManager getInstance() {
        return null == instance ? instance = new TAPContactManager() : instance;
    }

    public TAPUserModel getUserData(String userID) {
        return getUserDataMap().get(userID);
    }

    public void updateUserDataMap(TAPUserModel user) {
        if (null == getUserDataMap().get(user.getUserID())) {
            // Add new user to map
            user.setIsContact(0);
            getUserDataMap().put(user.getUserID(), user);
        } else {
            // Update user data in map
            getUserDataMap().get(user.getUserID()).updateValue(user);
        }
    }

    public void loadAllUserDataFromDatabase() {
        TAPDataManager.getInstance().getAllUserData(getAllUserDataListener);
    }

    public void saveUserDataToDatabase(TAPUserModel userModel) {
        TAPDataManager.getInstance().checkContactAndInsertToDatabase(userModel);
    }

    public void saveUserDataMapToDatabase() {
        TAPDataManager.getInstance().insertMyContactToDatabase(convertUserDataToList(userDataMap));
    }

    public void refreshUserDataMap() {
        getUserDataMap().clear();
        loadAllUserDataFromDatabase();
    }

    private HashMap<String, TAPUserModel> getUserDataMap() {
        return null == userDataMap ? userDataMap = new HashMap<>() : userDataMap;
    }

    private List<TAPUserModel> convertUserDataToList(HashMap<String, TAPUserModel> userModelMap) {
        List<TAPUserModel> userModelList = new ArrayList<>();
        for (Map.Entry<String, TAPUserModel> entry : userModelMap.entrySet()) {
            userModelList.add(entry.getValue());
        }
        return userModelList;
    }

    private HashMap<String, TAPUserModel> convertUserDataToMap(List<TAPUserModel> userModelList) {
        HashMap<String, TAPUserModel> userModelMap = new HashMap<>();
        for (TAPUserModel userModel : userModelList) {
            userModelMap.put(userModel.getUserID(), userModel);
        }
        return userModelMap;
    }

    private TAPDatabaseListener<TAPUserModel> getAllUserDataListener = new TAPDatabaseListener<TAPUserModel>() {
        @Override
        public void onSelectFinished(List<TAPUserModel> entities) {
            userDataMap = convertUserDataToMap(entities);
        }
    };
}

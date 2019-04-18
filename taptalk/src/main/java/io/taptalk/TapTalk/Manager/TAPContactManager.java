package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPContactManager {

    private static final String TAG = TAPContactManager.class.getSimpleName();
    private static TAPContactManager instance;
    private HashMap<String, TAPUserModel> userDataMap;
    private HashMap<String, TAPUserModel> userMapByPhoneNumber;

    private TAPContactManager() {
        //loadAllUserDataFromDatabase();
        TAPConnectionManager.getInstance().addSocketListener(new TAPSocketListener() {
            @Override
            public void onSocketConnected() {
                loadAllUserDataFromDatabase();
            }

            @Override
            public void onSocketDisconnected() {
                saveUserDataMapToDatabase();
            }
        });
    }

    public static TAPContactManager getInstance() {
        return null == instance ? instance = new TAPContactManager() : instance;
    }

    public TAPUserModel getUserData(String userID) {
        return getUserDataMap().get(userID);
    }

    public void updateUserDataMap(TAPUserModel user) {
        if (!user.getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID()) && null == getUserDataMap().get(user.getUserID())) {
            // Add new user to map
            user.setIsContact(0);
            getUserDataMap().put(user.getUserID(), user);
        } else if (!user.getUserID().equals(TAPDataManager.getInstance().getActiveUser().getUserID()) && null != getUserDataMap().get(user.getUserID())) {
            // Update user data in map
            getUserDataMap().get(user.getUserID()).updateValue(user);
        }
    }

    public void updateUserDataMap(List<TAPUserModel> users) {
        for (TAPUserModel user : users) {
            updateUserDataMap(user);
        }
    }

    public void saveUserDataToDatabase(TAPUserModel userModel) {
        if (!userModel.getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
            TAPDataManager.getInstance().checkContactAndInsertToDatabase(userModel);
        }
    }

    public void loadAllUserDataFromDatabase() {
        TAPDataManager.getInstance().getAllUserData(getAllUserDataListener);
    }

    public void saveUserDataMapToDatabase() {
        TAPDataManager.getInstance().insertMyContactToDatabase(convertUserDataToList(userDataMap));
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

    public HashMap<String, TAPUserModel> getUserMapByPhoneNumber() {
        return null == userMapByPhoneNumber ? userMapByPhoneNumber = new HashMap<>() : userMapByPhoneNumber;
    }

    public void setUserMapByPhoneNumber(HashMap<String, TAPUserModel> userMapByPhoneNumber) {
        this.userMapByPhoneNumber = userMapByPhoneNumber;
    }

    public void addUserMapByPhoneNumber(TAPUserModel userModel) {
        if (null != userModel.getPhoneWithCode() && !"".equals(userModel.getPhoneWithCode()))
            getUserMapByPhoneNumber().put(userModel.getPhoneNumber(), userModel);
    }

    public boolean isUserPhoneNumberAlreadyExist(String phone) {
        return getUserMapByPhoneNumber().containsKey(phone);
    }
}

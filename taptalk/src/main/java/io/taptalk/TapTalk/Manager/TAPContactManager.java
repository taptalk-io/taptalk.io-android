package io.taptalk.TapTalk.Manager;

import android.util.Log;

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

    private TAPContactManager() {
        loadAllUserDataFromDatabase();
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
        Log.e(TAG, "getUserData: "+userID );
        return getUserDataMap().get(userID);
    }

    public void updateUserDataMap(TAPUserModel user) {
        Log.e(TAG, "updateUserDataMap: "+ user.getUserID() );
        if (!user.getUserID().equals(TAPDataManager.getInstance().getActiveUser().getUserID()) && null == getUserDataMap().get(user.getUserID())) {
            // Add new user to map
            user.setIsContact(0);
            getUserDataMap().put(user.getUserID(), user);
        } else if (!user.getUserID().equals(TAPDataManager.getInstance().getActiveUser().getUserID())) {
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
        if (!userModel.getUserID().equals(TAPDataManager.getInstance().getActiveUser().getUserID())) {
            TAPDataManager.getInstance().checkContactAndInsertToDatabase(userModel);
        }
    }

    private void loadAllUserDataFromDatabase() {
        TAPDataManager.getInstance().getAllUserData(getAllUserDataListener);
    }

    private void saveUserDataMapToDatabase() {
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

}

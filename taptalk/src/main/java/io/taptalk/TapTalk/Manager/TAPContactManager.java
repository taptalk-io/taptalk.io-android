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
    private static HashMap<String, TAPContactManager> instances;

    private String instanceKey = "";
    private HashMap<String, TAPUserModel> userDataMap;
    private HashMap<String, TAPUserModel> userMapByPhoneNumber;
    private HashMap<String, TAPUserModel> tempUserDataMapByUsername;
    private String myCountryCode;
    private boolean isContactSyncPermissionAsked, isContactSyncAllowedByUser;

    private TAPContactManager(String instanceKey) {
        this.instanceKey = instanceKey;
        //loadAllUserDataFromDatabase();
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(new TAPSocketListener() {
//            @Override
//            public void onSocketConnected() {
//                //loadAllUserDataFromDatabase();
//            }

            @Override
            public void onSocketDisconnected() {
                saveUserDataMapToDatabase();
            }
        });
    }

    public static TAPContactManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPContactManager instance = new TAPContactManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPContactManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPUserModel getUserData(String userID) {
        return getUserDataMap().get(userID);
    }

    public void updateUserData(TAPUserModel user) {
        String myUserId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
        String incomingUserId = user.getUserID();
        TAPUserModel existingUser = getUserDataMap().get(incomingUserId);
        if (!incomingUserId.equals(myUserId) && null == existingUser) {
            // Add new user to map
            user.checkAndSetContact(0);
            getUserDataMap().put(incomingUserId, user);
            saveUserDataToDatabase(user);
        } else if (!incomingUserId.equals(myUserId) &&
                null != existingUser.getUpdated() &&
                null != user.getUpdated() &&
                existingUser.getUpdated() <= user.getUpdated()) {
            // Update user data in map
            existingUser.updateValue(user);
            saveUserDataToDatabase(user);
        }
    }

    public void updateUserData(List<TAPUserModel> users) {
        for (TAPUserModel user : users) {
            updateUserData(user);
        }
    }

    public void removeFromContacts(String userID) {
        getUserData(userID).setIsContact(0);
        TAPDataManager.getInstance(instanceKey).insertMyContactToDatabase(getUserData(userID));
    }

    public void saveUserDataToDatabase(TAPUserModel userModel) {
        if (!userModel.getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
            TAPDataManager.getInstance(instanceKey).checkContactAndInsertToDatabase(userModel);
        }
    }

    public void loadAllUserDataFromDatabase() {
        TAPDataManager.getInstance(instanceKey).getAllUserData(getAllUserDataListener);
    }

    public void saveUserDataMapToDatabase() {
        TAPDataManager.getInstance(instanceKey).insertMyContactToDatabase(convertUserDataToList(getUserDataMap()));
    }

    private HashMap<String, TAPUserModel> getUserDataMap() {
        return null == userDataMap ? userDataMap = new HashMap<>() : userDataMap;
    }

    public void clearUserDataMap() {
        getUserDataMap().clear();
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

    public void clearUserMapByPhoneNumber() {
        getUserMapByPhoneNumber().clear();
    }

    public void addUserMapByPhoneNumber(TAPUserModel userModel) {
        if (null != userModel.getPhoneWithCode() && !"".equals(userModel.getPhoneWithCode()))
            getUserMapByPhoneNumber().put(userModel.getPhoneWithCode(), userModel);
    }

    public boolean isUserPhoneNumberAlreadyExist(String phone) {
        return getUserMapByPhoneNumber().containsKey(phone);
    }

    public String convertPhoneNumber(String phone) {
        if (phone.contains("*") || phone.contains("#") || phone.contains(";") || phone.contains(",") || phone.isEmpty()) {
            return "";
        }
        String tempPhone = phone.replaceAll("[^\\d]", "");
        String prefix = tempPhone.substring(0, getMyCountryCode().length());

        if ('0' == tempPhone.charAt(0)) {
            tempPhone = tempPhone.replaceFirst("0", getMyCountryCode());
        } else if (!prefix.equals(getMyCountryCode())) {
            tempPhone = getMyCountryCode() + tempPhone;
        }

        return tempPhone;
    }

    public HashMap<String, TAPUserModel> getTempUserDataMapByUsername() {
        return null == tempUserDataMapByUsername ? tempUserDataMapByUsername = new HashMap<>() : tempUserDataMapByUsername;
    }

    public String getMyCountryCode() {
        return null == myCountryCode ? myCountryCode = "62" : myCountryCode;
    }

    public void resetMyCountryCode() {
        setMyCountryCode(null);
    }

    public void setMyCountryCode(String myCountryCode) {
        this.myCountryCode = myCountryCode;
    }

    public boolean isContactSyncPermissionAsked() {
        return isContactSyncPermissionAsked;
    }

    public void setAndSaveContactSyncPermissionAsked(boolean contactSyncPermissionAsked) {
        TAPDataManager.getInstance(instanceKey).saveContactSyncPermissionAsked(contactSyncPermissionAsked);
        isContactSyncPermissionAsked = contactSyncPermissionAsked;
    }

    public void setContactSyncPermissionAsked(boolean contactSyncPermissionAsked) {
        isContactSyncPermissionAsked = contactSyncPermissionAsked;
    }

    public void resetContactSyncPermissionAsked() {
        setContactSyncPermissionAsked(false);
    }

    public boolean isContactSyncAllowedByUser() {
        return isContactSyncAllowedByUser;
    }

    public void setAndSaveContactSyncAllowedByUser(boolean contactSyncAllowedByUser) {
        TAPDataManager.getInstance(instanceKey).saveContactSyncAllowedByUser(contactSyncAllowedByUser);
        isContactSyncAllowedByUser = contactSyncAllowedByUser;
    }

    public void setContactSyncAllowedByUser(boolean contactSyncAllowedByUser) {
        isContactSyncAllowedByUser = contactSyncAllowedByUser;
    }

    public void resetContactSyncAllowedByUser() {
        setContactSyncAllowedByUser(false);
    }
}

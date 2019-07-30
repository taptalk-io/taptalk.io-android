package io.taptalk.TapTalk.Helper;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Interface.TapContactInterface;
import io.taptalk.TapTalk.Interface.TapContactListInterface;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapCoreContactManager {

    public static void getAllUserContacts(TapContactListInterface tapContactListInterface) {
        TAPDataManager.getInstance().getMyContactList(new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(List<TAPUserModel> entities) {
                tapContactListInterface.onSuccess(entities);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                tapContactListInterface.onError(errorMessage);
            }
        });
    }

    public static void getUserDataWithUserID(String userID, TapContactInterface tapContactInterface) {
        TAPDataManager.getInstance().getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                tapContactInterface.onSuccess(response.getUser());
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapContactInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapContactInterface.onError(errorMessage);
            }
        });
    }

    public static void getUserDataWithXCUserID(String xcUserID, TapContactInterface tapContactInterface) {
        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                tapContactInterface.onSuccess(response.getUser());
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapContactInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapContactInterface.onError(errorMessage);
            }
        });
    }

    public static void saveUserData(TAPUserModel tapUserModel) {
        TAPContactManager.getInstance().updateUserData(tapUserModel);
    }

    public static void addToTapTalkContactsWithUserID(String userID, TapContactInterface tapContactInterface) {
        TAPDataManager.getInstance().addContactApi(userID, new TAPDefaultDataView<TAPAddContactResponse>() {
            @Override
            public void onSuccess(TAPAddContactResponse response) {
                tapContactInterface.onSuccess(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapContactInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapContactInterface.onError(errorMessage);
            }
        });
    }

    public static void addToTapTalkContactsWithPhoneNumber(String phoneNumber, TapContactInterface tapContactInterface) {
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(phoneNumber);
        TAPDataManager.getInstance().addContactByPhone(phoneNumberList, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                tapContactInterface.onSuccess(response.getUsers().get(0));
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapContactInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapContactInterface.onError(errorMessage);
            }
        });
    }

    public static void removeFromTapTalkContacts(String userID, TapTalkActionInterface tapTalkActionInterface) {
        TAPDataManager.getInstance().removeContactApi(userID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                tapTalkActionInterface.onSuccess(response.getMessage());
                TAPContactManager.getInstance().removeFromContacts(userID);
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapTalkActionInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapTalkActionInterface.onError(errorMessage);
            }
        });
    }
}
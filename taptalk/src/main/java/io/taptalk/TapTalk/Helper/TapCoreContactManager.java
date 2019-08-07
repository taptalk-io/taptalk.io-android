package io.taptalk.TapTalk.Helper;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Interface.TapCommonInterface;
import io.taptalk.TapTalk.Interface.TapContactInterface;
import io.taptalk.TapTalk.Interface.TapContactListInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;

public class TapCoreContactManager {

    public static void getAllUserContacts(TapContactListInterface listener) {
        TAPDataManager.getInstance().getMyContactList(new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(List<TAPUserModel> entities) {
                listener.onSuccess(entities);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }

    public static void getUserDataWithUserID(String userID, TapContactInterface listener) {
        TAPDataManager.getInstance().getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                listener.onSuccess(response.getUser());
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }

    public static void getUserDataWithXCUserID(String xcUserID, TapContactInterface listener) {
        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                listener.onSuccess(response.getUser());
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }

    public static void saveUserData(TAPUserModel tapUserModel) {
        TAPContactManager.getInstance().updateUserData(tapUserModel);
    }

    public static void addToTapTalkContactsWithUserID(String userID, TapContactInterface listener) {
        TAPDataManager.getInstance().addContactApi(userID, new TAPDefaultDataView<TAPAddContactResponse>() {
            @Override
            public void onSuccess(TAPAddContactResponse response) {
                listener.onSuccess(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }

    public static void addToTapTalkContactsWithPhoneNumber(String phoneNumber, TapContactInterface listener) {
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(phoneNumber);
        TAPDataManager.getInstance().addContactByPhone(phoneNumberList, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                listener.onSuccess(response.getUsers().get(0));
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }

    public static void removeFromTapTalkContacts(String userID, TapCommonInterface listener) {
        TAPDataManager.getInstance().removeContactApi(userID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                listener.onSuccess(response.getMessage());
                TAPContactManager.getInstance().removeFromContacts(userID);
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }
}
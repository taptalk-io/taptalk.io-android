package io.taptalk.TapTalk.Manager;

import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMultipleContactListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;

@Keep
public class TapCoreContactManager {

    private static TapCoreContactManager instance;

    public static TapCoreContactManager getInstance() {
        return null == instance ? instance = new TapCoreContactManager() : instance;
    }

    public void getAllUserContacts(TapCoreGetMultipleContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance().getMyContactList(new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(List<TAPUserModel> entities) {
                if (null != listener) {
                    listener.onSuccess(entities);
                }
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getUserDataWithUserID(String userID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance().getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getUserDataWithXCUserID(String xcUserID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
                TAPContactManager.getInstance().updateUserData(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void saveUserData(TAPUserModel user) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPContactManager.getInstance().updateUserData(user);
    }

    public void addToTapTalkContactsWithUserID(String userID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance().addContactApi(userID, new TAPDefaultDataView<TAPAddContactResponse>() {
            @Override
            public void onSuccess(TAPAddContactResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void addToTapTalkContactsWithPhoneNumber(String phoneNumber, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(phoneNumber);
        TAPDataManager.getInstance().addContactByPhone(phoneNumberList, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUsers().get(0));
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void removeFromTapTalkContacts(String userID, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance().removeContactApi(userID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
                TAPContactManager.getInstance().removeFromContacts(userID);
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }
}
package io.taptalk.TapTalk.Manager;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
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
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.ROOM_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.SECTION_TITLE;

@Keep
public class TapCoreContactManager {

    private static HashMap<String, TapCoreContactManager> instances;

    private String instanceKey = "";

    public TapCoreContactManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapCoreContactManager getInstance() {
        return getInstance("");
    }

    public static TapCoreContactManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapCoreContactManager instance = new TapCoreContactManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapCoreContactManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public void getAllUserContacts(TapCoreGetMultipleContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getMyContactList(new TAPDatabaseListener<TAPUserModel>() {
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
                TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getUserByXcUserIdFromApi(xcUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
                TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
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
        TAPContactManager.getInstance(instanceKey).updateUserData(user);
    }

    public void addToTapTalkContactsWithUserID(String userID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).addContactApi(userID, new TAPDefaultDataView<TAPAddContactResponse>() {
            @Override
            public void onSuccess(TAPAddContactResponse response) {
                if (null != response.getUser()) {
                    if (null != listener) {
                        listener.onSuccess(response.getUser());
                    }
                    TAPUserModel newContact = response.getUser().setUserAsContact();
                    TAPContactManager.getInstance(instanceKey).updateUserData(newContact);
                } else {
                    if (null != listener) {
                        listener.onError(ERROR_CODE_OTHERS, "User already added as contact.");
                    }
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(phoneNumber);
        TAPDataManager.getInstance(instanceKey).addContactByPhone(phoneNumberList, new TAPDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                if (null != response && null != response.getUsers() && !response.getUsers().isEmpty()) {
                    try {
                        List<TAPUserModel> users = new ArrayList<>();
                        for (TAPUserModel contact : response.getUsers()) {
                            if (!contact.getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                contact.setUserAsContact();
                                users.add(contact);
                            }
                            if (null != listener) {
                                listener.onSuccess(contact);
                            }
                        }
                        TAPContactManager.getInstance(instanceKey).saveContactListToDatabase(users);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (null != listener) {
                            listener.onError(ERROR_CODE_OTHERS, e.getLocalizedMessage());
                        }
                    }
                } else {
                    if (null != listener) {
                        listener.onError(ERROR_CODE_OTHERS, "User already added as contact or provided phone number is invalid.");
                    }
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).removeContactApi(userID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
                TAPContactManager.getInstance(instanceKey).removeFromContacts(userID);
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

    public void searchLocalContactByName(String keyword, TapCoreGetMultipleContactListener listener) {
        TAPDataManager.getInstance(instanceKey).searchContactsByName(keyword, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(List<TAPUserModel> entities) {
                if (null != listener) {
                    listener.onSuccess(entities);
                }
            }
        });
    }

    public void updateBio(String bio, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }

        TAPDataManager.getInstance(instanceKey).updateBio(bio, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    // TODO: 24/02/22 handle onsuccess listener MU
                }
                // TODO: 24/02/22 save active user MU
//                TapCoreContactManager.getInstance(instanceKey).saveUserData();
            }

            @Override
            public void onError(TAPErrorModel error) {
                super.onError(error);
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                super.onError(errorMessage);
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }
}

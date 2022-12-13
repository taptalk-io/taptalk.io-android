package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMultipleContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapRoomModelsResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public class TapCoreContactManager {

    private static HashMap<String, TapCoreContactManager> instances;

    private List<TapCoreContactListener> coreContactListeners;

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

    private List<TapCoreContactListener> getCoreContactListeners() {
        return null == coreContactListeners ? coreContactListeners = new ArrayList<>() : coreContactListeners;
    }

    public void addContactListener(TapCoreContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getCoreContactListeners().remove(listener);
        getCoreContactListeners().add(listener);
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

    public TAPUserModel getLocalUserData(String userID) {
        TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
        if (userID.equals(activeUser.getUserID())) {
            return activeUser;
        }
        return TAPContactManager.getInstance(instanceKey).getUserData(userID);
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

    public void updateActiveUserBio(String bio, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }

        TAPDataManager.getInstance(instanceKey).updateBio(bio, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    listener.onSuccess(response.getUser());
                }
                TAPDataManager.getInstance(instanceKey).saveActiveUser(response.getUser());
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

    public void reportUser(String userID, String category, boolean isOtherCategory, String reason, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).submitUserReport(userID, category, isOtherCategory, reason, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
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

    public void reportMessage(String messageID, String roomID, String category, boolean isOtherCategory, String reason, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).submitMessageReport(messageID, roomID, category, isOtherCategory, reason, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
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

    public void triggerContactBlocked(TAPUserModel userModel) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        for (TapCoreContactListener listener : getCoreContactListeners()) {
            listener.onContactBlocked(userModel);
        }
    }

    public void triggerContactUnblocked(TAPUserModel userModel) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        for (TapCoreContactListener listener : getCoreContactListeners()) {
            listener.onContactUnblocked(userModel);
        }
    }

    public void blockUser(String userID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).blockUser(userID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                TAPUserModel user  = TAPContactManager.getInstance(instanceKey).getUserData(userID);
                triggerContactBlocked(user);
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

    public void unblockUser(String userID, TapCoreGetContactListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).unblockUser(userID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                TAPUserModel user  = TAPContactManager.getInstance(instanceKey).getUserData(userID);
                triggerContactUnblocked(user);
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

    public void getBlockedUserList(TapCoreGetMultipleContactListener listener) {
        TAPDataManager.getInstance(instanceKey).getBlockedUserList(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPGetMultipleUserResponse response) {
                super.onSuccess(response);
                listener.onSuccess(response.getUsers());
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

    public void getBlockedUserIDs(TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getBlockedUserIds(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                super.onSuccess(response);
                listener.onSuccess(new ArrayList<>(response.getUnreadRoomIDs()));
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

    public void getGroupsInCommon(String userID, TapCoreGetRoomArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getGroupsInCommon(userID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapRoomModelsResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    if (response.getRooms() != null)
                        listener.onSuccess(new ArrayList<>(response.getRooms()));
                    else {
                        listener.onSuccess(new ArrayList<>());
                    }
                }
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

package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.RequestModel.TAPAddContactByPhoneRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPAddRoomParticipantRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPAuthTicketRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPCommonRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPCreateRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPDeleteMessageRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPDeleteRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListByRoomAfterRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListByRoomBeforeRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMultipleUserByIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetRoomByXcRoomIDRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByUsernameRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByXcUserIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPOTPRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPOTPVerifyRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPPushNotificationRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPRegisterRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPSendCustomMessageRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUpdateBioRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUpdateMessageStatusRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUpdateRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUserIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapCreateScheduledMessageRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapIdsWithRoomIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapMessageIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapReportUserRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapRoomIdWithPagingRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapMessageIdsRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapRemovePhotoRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapRoomIdsRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapSetMainPhotoRequest;
import io.taptalk.TapTalk.Model.RequestModel.TapStarMessageRequest;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAuthTicketResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCheckUsernameResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPDeleteMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapCheckDeleteAccountStateResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapCreateScheduledMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMessageDetailResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMutedRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetPhotoListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetRoomIdsWithStateResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetScheduledMessageListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetSharedContentResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapPinMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapRoomModelsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel;
import io.taptalk.TapTalk.Model.ResponseModel.TapStarMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapUnstarMessageResponse;
import io.taptalk.TapTalk.Model.TapConfigs;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkApiService {
    //String BASE_URL = "dev.taptalk.io:8080/api/v1/";

    @POST("server/auth_ticket/request")
    Observable<TAPBaseResponse<TAPAuthTicketResponse>> getAuthTicket(@Body TAPAuthTicketRequest request);

    @POST("auth/access_token/request")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> getAccessToken(@Header("Authorization") String authTicket);

    @POST("client/login/request_otp/v1_6")
    Observable<TAPBaseResponse<TAPOTPResponse>> requestOTPLogin(@Body TAPOTPRequest request);

    @POST("client/login/verify_otp")
    Observable<TAPBaseResponse<TAPLoginOTPVerifyResponse>> verifyingOTPLogin(@Body TAPOTPVerifyRequest request);

    @POST("chat/message/send/custom")
    Observable<TAPBaseResponse<TAPCommonResponse>> sendCustomMessage(@Body TAPSendCustomMessageRequest request);

    @POST("chat/message/room_list_and_unread")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getRoomList(@Body TAPCommonRequest request);

    @POST("chat/message/new_and_updated")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getPendingAndUpdatedMessage();

    @POST("chat/message/list_by_room/before")
    Observable<TAPBaseResponse<TAPGetMessageListByRoomResponse>> getMessageListByRoomBefore(@Body TAPGetMessageListByRoomBeforeRequest request);

    @POST("chat/message/feedback/delivered")
    Observable<TAPBaseResponse<TAPUpdateMessageStatusResponse>> updateMessageStatusAsDelivered(@Body TAPUpdateMessageStatusRequest request);

    @POST("chat/message/feedback/read")
    Observable<TAPBaseResponse<TAPUpdateMessageStatusResponse>> updateMessageStatusAsRead(@Body TAPUpdateMessageStatusRequest request);

    @POST("chat/message/list_by_room/after")
    Observable<TAPBaseResponse<TAPGetMessageListByRoomResponse>> getMessageListByRoomAfter(@Body TAPGetMessageListByRoomAfterRequest request);

    @POST("chat/message/delete")
    Observable<TAPBaseResponse<TAPDeleteMessageResponse>> deleteMessages(@Body TAPDeleteMessageRequest request);

    @POST("client/contact/list")
    Observable<TAPBaseResponse<TAPContactResponse>> getMyContactListFromAPI();

    @POST("client/contact/add")
    Observable<TAPBaseResponse<TAPAddContactResponse>> addContact(@Body TAPUserIdRequest request);

    @POST("client/contact/remove")
    Observable<TAPBaseResponse<TAPCommonResponse>> removeContact(@Body TAPUserIdRequest request);

    @POST("client/push_notification/update")
    Observable<TAPBaseResponse<TAPCommonResponse>> registerFcmTokenToServer(@Body TAPPushNotificationRequest request);

    @POST("client/user/get_by_username")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByUsername(@Body TAPGetUserByUsernameRequest request);

    @POST("client/user/get_by_id")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByID(@Body TapIdRequest request);

    @POST("client/user/get_by_xcuserid")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByXcUserID(@Body TAPGetUserByXcUserIdRequest request);

    @POST("client/user/get_all_by_ids")
    Observable<TAPBaseResponse<TAPGetMultipleUserResponse>> getMultipleUserByID(@Body TAPGetMultipleUserByIdRequest request);

    @POST("client/country/list")
    Observable<TAPBaseResponse<TAPCountryListResponse>> getCountryList();

    @POST("client/register")
    Observable<TAPBaseResponse<TAPRegisterResponse>> register(@Body TAPRegisterRequest request);

    @POST("client/logout")
    Observable<TAPBaseResponse<TAPCommonResponse>> logout();

    @POST("client/user/exists/username")
    Observable<TAPBaseResponse<TAPCheckUsernameResponse>> checkUsernameExists(@Body TAPGetUserByUsernameRequest request);

    @POST("client/contact/add_by_phones")
    Observable<TAPBaseResponse<TAPAddContactByPhoneResponse>> addContactByPhone(@Body TAPAddContactByPhoneRequest request);

    @POST("client/room/create")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> createChatRoom(@Body TAPCreateRoomRequest request);

    @POST("client/room/get")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> getChatRoomData(@Body TAPCommonRequest request);

    @POST("client/room/get_by_xc_room_id")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> getChatRoomByXcRoomID(@Body TAPGetRoomByXcRoomIDRequest request);

    @POST("client/room/update")
    Observable<TAPBaseResponse<TAPUpdateRoomResponse>> updateChatRoom(@Body TAPUpdateRoomRequest request);

    @POST("client/room/participants/add")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> addRoomParticipant(@Body TAPAddRoomParticipantRequest request);

    @POST("client/room/participants/remove")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> removeRoomParticipant(@Body TAPAddRoomParticipantRequest request);

    @POST("client/room/leave")
    Observable<TAPBaseResponse<TAPCommonResponse>> leaveChatRoom(@Body TAPCommonRequest request);

    @POST("client/room/admins/promote")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> promoteGroupAdmins(@Body TAPAddRoomParticipantRequest request);

    @POST("client/room/admins/demote")
    Observable<TAPBaseResponse<TAPCreateRoomResponse>> demoteGroupAdmins(@Body TAPAddRoomParticipantRequest request);

    @POST("client/room/delete")
    Observable<TAPBaseResponse<TAPCommonResponse>> deleteChatRoom(@Body TAPDeleteRoomRequest request);

    @POST("client/project_configs")
    Observable<TAPBaseResponse<TapConfigs>> getProjectConfig();

    @POST("client/user/update_bio")
    Observable<TAPBaseResponse<TAPGetUserResponse>> updateBio(@Body TAPUpdateBioRequest request);

    @POST("client/user/photo/get_list")
    Observable<TAPBaseResponse<TapGetPhotoListResponse>> getPhotoList(@Body TAPUserIdRequest request);

    @POST("client/user/photo/set_main")
    Observable<TAPBaseResponse<TAPGetUserResponse>> setMainPhoto(@Body TapSetMainPhotoRequest request);

    @POST("client/user/photo/delete")
    Observable<TAPBaseResponse<TAPGetUserResponse>> removePhoto(@Body TapRemovePhotoRequest request);

    @POST("chat/message/star")
    Observable<TAPBaseResponse<TapStarMessageResponse>> starMessage(@Body TapStarMessageRequest request);

    @POST("chat/message/unstar")
    Observable<TAPBaseResponse<TapUnstarMessageResponse>> unStarMessage(@Body TapStarMessageRequest request);

    @POST("chat/message/get_starred_list")
    Observable<TAPBaseResponse<TAPGetMessageListByRoomResponse>> getStarredMessages(@Body TapRoomIdWithPagingRequest request);

    @POST("chat/message/get_starred_ids")
    Observable<TAPBaseResponse<TapStarMessageResponse>> getStarredMessageIds(@Body TAPCommonRequest request);

    @POST("client/room/mark_as_unread")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> markRoomAsUnread(@Body TapRoomIdsRequest request);

    @POST("client/room/get_unread_room_ids")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> getUnreadRoomIds();

    @POST("client/user/delete_account/request_otp")
    Observable<TAPBaseResponse<TAPOTPResponse>> requestDeleteAccountOtp(@Body TAPOTPRequest request);

    @POST("client/user/delete_account/check_state")
    Observable<TAPBaseResponse<TapCheckDeleteAccountStateResponse>> checkDeleteAccountState();

    @POST("client/user/delete_account/verify_otp")
    Observable<TAPBaseResponse<TAPCommonResponse>> verifyOtpDeleteAccount(@Body TAPOTPVerifyRequest request);

    @POST("chat/message/get_pinned_list")
    Observable<TAPBaseResponse<TAPGetMessageListByRoomResponse>> getPinnedMessages(@Body TapRoomIdWithPagingRequest request);

    @POST("chat/message/get_pinned_ids")
    Observable<TAPBaseResponse<TapPinMessageResponse>> getPinnedMessageIds(@Body TAPCommonRequest request);

    @POST("chat/message/pin")
    Observable<TAPBaseResponse<TapPinMessageResponse>> pinMessages(@Body TapMessageIdsRequest request);

    @POST("chat/message/unpin")
    Observable<TAPBaseResponse<TapPinMessageResponse>> unpinMessages(@Body TapMessageIdsRequest request);

    @POST("chat/room/get_shared_content")
    Observable<TAPBaseResponse<TapGetSharedContentResponse>> getSharedMedia(@Body TAPGetMessageListByRoomAfterRequest request);

    @POST("client/room/get_muted_room_ids")
    Observable<TAPBaseResponse<TapGetMutedRoomIdsResponse>> getMutedRoomIds();

    @POST("client/room/mute")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> muteRoom(@Body TapRoomIdsRequest request);

    @POST("client/room/unmute")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> unmuteRoom(@Body TapRoomIdsRequest request);

    @POST("client/room/get_pinned_room_ids")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> getPinnedRoomIds();

    @POST("client/room/pin")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> pinRoom(@Body TapRoomIdsRequest request);

    @POST("client/room/unpin")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> unpinRoom(@Body TapRoomIdsRequest request);

    @POST("client/room/clear_chat")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> clearChat(@Body TapRoomIdsRequest request);

    @POST("client/room/get_room_ids_with_state")
    Observable<TAPBaseResponse<TapGetRoomIdsWithStateResponse>> getRoomIdsWithState();

    @POST("chat/scheduled_message/create")
    Observable<TAPBaseResponse<TapCreateScheduledMessageResponse>> createScheduledMessage(@Body TapCreateScheduledMessageRequest request);

    @POST("chat/scheduled_message/get_scheduled_list")
    Observable<TAPBaseResponse<TapGetScheduledMessageListResponse>> getScheduledMessageList(@Body TAPCommonRequest request);

    @POST("chat/scheduled_message/edit_content")
    Observable<TAPBaseResponse<TAPCommonResponse>> editScheduledMessageContent(@Body TapCreateScheduledMessageRequest request);

    @POST("chat/scheduled_message/edit_scheduled_time")
    Observable<TAPBaseResponse<TAPCommonResponse>> editScheduledMessageTime(@Body TapScheduledMessageModel request);

    @POST("chat/scheduled_message/delete")
    Observable<TAPBaseResponse<TapIdsResponse>> deleteScheduledMessages(@Body TapIdsWithRoomIdRequest request);

    @POST("chat/scheduled_message/send_now")
    Observable<TAPBaseResponse<TapIdsResponse>> sendScheduledMessageNow(@Body TapIdsWithRoomIdRequest request);

    @POST("client/chat_report/submit_user")
    Observable<TAPBaseResponse<TAPCommonResponse>> submitUserReport(@Body TapReportUserRequest request);

    @POST("client/chat_report/submit_message")
    Observable<TAPBaseResponse<TAPCommonResponse>> submitMessageReport(@Body TapReportUserRequest request);

    @POST("client/contact/block")
    Observable<TAPBaseResponse<TAPCommonResponse>> blockUser(@Body TAPUserIdRequest request);

    @POST("client/contact/unblock")
    Observable<TAPBaseResponse<TAPCommonResponse>> unblockUser(@Body TAPUserIdRequest request);

    @POST("client/contact/get_blocked_list")
    Observable<TAPBaseResponse<TAPGetMultipleUserResponse>> getBlockedUserList();

    @POST("client/contact/get_blocked_user_ids")
    Observable<TAPBaseResponse<TapGetUnreadRoomIdsResponse>> getBlockedUserIds();

    @POST("chat/message/get_details")
    Observable<TAPBaseResponse<TapGetMessageDetailResponse>> getMessageDetails(@Body TapMessageIdRequest request);

    @POST("client/room/groups_in_common")
    Observable<TAPBaseResponse<TapRoomModelsResponse>> getGroupsInCommon(@Body TAPUserIdRequest request);
}

package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.RequestModel.TAPAddContactByPhoneRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPAddRoomParticipantRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPAuthTicketRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPCommonRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPCreateRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPDeleteMessageRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPDeleteRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListbyRoomAfterRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListByRoomBeforeRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMultipleUserByIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByUsernameRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByXcUserIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPLoginOTPRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPLoginOTPVerifyRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPPushNotificationRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPRegisterRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPSendCustomMessageRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUpdateMessageStatusRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUpdateRoomRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUserIdRequest;
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
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPProjectConfigResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPSendCustomMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkApiService {
    //String BASE_URL = "dev.taptalk.io:8080/api/v1/";

    @POST("server/auth_ticket/request")
    Observable<TAPBaseResponse<TAPAuthTicketResponse>> getAuthTicket(@Body TAPAuthTicketRequest request);

    @POST("server/message/send/custom")
    Observable<TAPBaseResponse<TAPSendCustomMessageResponse>> sendCustomMessage(@Body TAPSendCustomMessageRequest request);

    @POST("auth/access_token/request")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> getAccessToken();

    @POST("client/login/request_otp")
    Observable<TAPBaseResponse<TAPLoginOTPResponse>> requestOTPLogin(@Body TAPLoginOTPRequest request);

    @POST("client/login/verify_otp")
    Observable<TAPBaseResponse<TAPLoginOTPVerifyResponse>> verifyingOTPLogin(@Body TAPLoginOTPVerifyRequest request);

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
    Observable<TAPBaseResponse<TAPGetMessageListByRoomResponse>> getMessageListByRoomAfter(@Body TAPGetMessageListbyRoomAfterRequest request);

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
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByID(@Body TAPGetUserByIdRequest request);

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
    Observable<TAPBaseResponse<TAPProjectConfigResponse>> getProjectConfig();
}

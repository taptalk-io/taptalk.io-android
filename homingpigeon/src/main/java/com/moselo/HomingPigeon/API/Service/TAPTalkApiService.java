package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Model.RequestModel.TAPAuthTicketRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPCommonRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPGetMessageListbyRoomAfterRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPGetMessageListbyRoomBeforeRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPGetUserByIdRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPGetUserByUsernameRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPGetUserByXcUserIdRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPPushNotificationRequest;
import com.moselo.HomingPigeon.Model.RequestModel.TAPUserIdRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPBaseResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPCommonResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPContactResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPGetRoomListResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPGetUserResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkApiService {
    String BASE_URL = BuildConfig.BASE_URL_API;
    //String BASE_URL = "dev.taptalk.io:8080/api/v1/";

    @POST("server/auth_ticket/request")
    Observable<TAPBaseResponse<TAPAuthTicketResponse>> getAuthTicket(@Body TAPAuthTicketRequest request);

    @POST("auth/access_token/request")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> getAccessToken();

    @POST("chat/message/room_list_and_unread")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getRoomList(@Body TAPCommonRequest request);

    @POST("chat/message/new_and_updated")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getPendingAndUpdatedMessage();

    @POST("chat/message/list_by_room/before")
    Observable<TAPBaseResponse<TAPGetMessageListbyRoomResponse>> getMessageListByRoomBefore(@Body TAPGetMessageListbyRoomBeforeRequest request);

    @POST("client/contact/list")
    Observable<TAPBaseResponse<TAPContactResponse>> getMyContactListFromAPI();

    @POST("client/push_notification/update")
    Observable<TAPBaseResponse<TAPCommonResponse>> registerFcmTokenToServer(@Body TAPPushNotificationRequest request);

    @POST("chat/message/list_by_room/after")
    Observable<TAPBaseResponse<TAPGetMessageListbyRoomResponse>> getMessageListByRoomAfter(@Body TAPGetMessageListbyRoomAfterRequest request);

    @POST("client/contact/add")
    Observable<TAPBaseResponse<TAPCommonResponse>> addContact(@Body TAPUserIdRequest request);

    @POST("client/contact/remove")
    Observable<TAPBaseResponse<TAPCommonResponse>> removeContact(@Body TAPUserIdRequest request);

    @POST("client/user/get_by_id")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByID(@Body TAPGetUserByIdRequest request);

    @POST("client/user/get_by_xcuserid")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByXcUserID(@Body TAPGetUserByXcUserIdRequest request);

    @POST("client/user/get_by_username")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByUsername(@Body TAPGetUserByUsernameRequest request);
}

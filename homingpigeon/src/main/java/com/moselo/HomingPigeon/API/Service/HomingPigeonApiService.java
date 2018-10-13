package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.Model.RequestModel.HpCommonRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomAfterRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomBeforeRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.RequestModel.HpAuthTicketRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonApiService {
    String BASE_URL = "https://hp-staging.moselo.com:8080/api/v1/";

    @POST("server/auth_ticket/request")
    Observable<BaseResponse<HpAuthTicketResponse>> getAuthTicket(@Body HpAuthTicketRequest request);

    @POST("auth/access_token/request")
    Observable<BaseResponse<HpGetAccessTokenResponse>> getAccessToken();

    @POST("chat/message/room_list_and_unread")
    Observable<BaseResponse<HpGetRoomListResponse>> getRoomList(@Body HpCommonRequest request);

    @POST("chat/message/list_by_room/after")
    Observable<BaseResponse<HpGetMessageListbyRoomResponse>> getMessageListByRoomAfter(@Body HpGetMessageListbyRoomAfterRequest request);

    @POST("chat/message/list_by_room/before")
    Observable<BaseResponse<HpGetMessageListbyRoomResponse>> getMessageListByRoomBefore(@Body HpGetMessageListbyRoomBeforeRequest request);
}

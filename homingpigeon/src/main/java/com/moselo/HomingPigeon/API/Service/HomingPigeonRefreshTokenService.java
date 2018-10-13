package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.Model.RequestModel.HpAuthTicketRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpCommonRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomAfterRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomBeforeRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonRefreshTokenService {
    String BASE_URL = "https://hp-staging.moselo.com:8080/api/v1/";

    @POST("auth/access_token/refresh")
    Observable<BaseResponse<HpGetAccessTokenResponse>> refreshAccessToken();
}

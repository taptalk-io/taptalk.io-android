package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.Model.ResponseModel.BaseResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;

import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonRefreshTokenService {
    String BASE_URL = "https://hp-dev.moselo.com:8080/api/v1/";

    @POST("auth/access_token/refresh")
    Observable<BaseResponse<HpGetAccessTokenResponse>> refreshAccessToken();
}

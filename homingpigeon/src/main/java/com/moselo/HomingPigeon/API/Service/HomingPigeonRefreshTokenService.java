package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Model.ResponseModel.BaseResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;

import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonRefreshTokenService {
    String BASE_URL = BuildConfig.BASE_URL_API;

    @POST("auth/access_token/refresh")
    Observable<BaseResponse<HpGetAccessTokenResponse>> refreshAccessToken();
}

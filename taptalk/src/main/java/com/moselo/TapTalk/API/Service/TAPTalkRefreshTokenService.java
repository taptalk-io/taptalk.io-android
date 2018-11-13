package com.moselo.TapTalk.API.Service;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPBaseResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPGetAccessTokenResponse;

import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkRefreshTokenService {
    String BASE_URL = BuildConfig.BASE_URL_API;

    @POST("auth/access_token/refresh")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> refreshAccessToken();
}

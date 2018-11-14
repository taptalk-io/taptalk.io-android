package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.Taptalk.BuildConfig;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkRefreshTokenService {
    String BASE_URL = BuildConfig.BASE_URL_API;

    @POST("auth/access_token/refresh")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> refreshAccessToken();
}

package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkRefreshTokenService {

    @POST("auth/access_token/refresh")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> refreshAccessToken();
}

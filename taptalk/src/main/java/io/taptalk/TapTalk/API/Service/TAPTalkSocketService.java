package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.Taptalk.BuildConfig;
import retrofit2.http.GET;
import rx.Observable;

public interface TAPTalkSocketService {
    String BASE_URL = BuildConfig.BASE_URL_SOCKET;

    @GET("pigeon?check=1")
    Observable<TAPBaseResponse<TAPErrorModel>> validateAccessToken();
}

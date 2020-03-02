package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import retrofit2.http.GET;
import rx.Observable;

public interface TAPTalkSocketService {

    @GET("connect?check=1")
    Observable<TAPBaseResponse<TAPErrorModel>> validateAccessToken();
}

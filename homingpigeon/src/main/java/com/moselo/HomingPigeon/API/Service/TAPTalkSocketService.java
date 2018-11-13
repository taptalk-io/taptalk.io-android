package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Model.TAPErrorModel;
import com.moselo.HomingPigeon.Model.ResponseModel.TAPBaseResponse;

import retrofit2.http.GET;
import rx.Observable;

public interface TAPTalkSocketService {
    String BASE_URL = BuildConfig.BASE_URL_SOCKET;

    @GET("pigeon?check=1")
    Observable<TAPBaseResponse<TAPErrorModel>> validateAccessToken();
}

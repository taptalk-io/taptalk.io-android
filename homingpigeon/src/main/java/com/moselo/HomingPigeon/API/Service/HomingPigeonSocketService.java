package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.ResponseModel.BaseResponse;

import retrofit2.http.GET;
import rx.Observable;

public interface HomingPigeonSocketService {
    String BASE_URL = BuildConfig.BASE_URL_SOCKET;

    @GET("pigeon?check=1")
    Observable<BaseResponse<HpErrorModel>> validateAccessToken();
}

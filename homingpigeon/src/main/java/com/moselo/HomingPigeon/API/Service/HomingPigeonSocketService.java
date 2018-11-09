package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.Model.ResponseModel.BaseResponse;
import com.moselo.HomingPigeon.Model.HpErrorModel;

import retrofit2.http.GET;
import rx.Observable;

public interface HomingPigeonSocketService {
    // TODO: 13/09/18 Change base URL
    String BASE_URL = "https://hp-dev.moselo.com:8080/";

    @GET("pigeon?check=1")
    Observable<BaseResponse<HpErrorModel>> validateAccessToken();
}

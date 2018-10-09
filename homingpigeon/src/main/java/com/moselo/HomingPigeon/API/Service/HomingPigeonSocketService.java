package com.moselo.HomingPigeon.API.Service;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.Model.ErrorModel;
import com.moselo.HomingPigeon.Model.RequestModel.AuthTicketRequest;
import com.moselo.HomingPigeon.Model.RequestModel.CommonRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetRoomListResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface HomingPigeonSocketService {
    // TODO: 13/09/18 Change base URL
    String BASE_URL = "https://hp-staging.moselo.com:8080/";

    @GET("pigeon?check=1")
    Observable<BaseResponse<ErrorModel>> validateAccessToken();
}

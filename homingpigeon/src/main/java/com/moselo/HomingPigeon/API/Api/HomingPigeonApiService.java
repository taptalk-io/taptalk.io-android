package com.moselo.HomingPigeon.API.Api;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.Model.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.RequestModel.AuthTicketRequest;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonApiService {
    // TODO: 13/09/18 Change base URL
    String BASE_URL = "https://hp-staging.moselo.com:8080/api/v1/";

    @POST("server/request_auth_ticket")
    Observable<BaseResponse<AuthTicketResponse>> getAuthTicket(@Body AuthTicketRequest request);
}

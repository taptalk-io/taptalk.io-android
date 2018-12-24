package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.Taptalk.BuildConfig;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkMultipartApiService {
    String BASE_URL = BuildConfig.BASE_URL_API;

    @POST("chat/file/upload")
    Observable<TAPBaseResponse<TAPUploadFileResponse>> uploadImage(@Body RequestBody uploadFile);

}

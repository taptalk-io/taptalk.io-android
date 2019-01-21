package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.RequestModel.TAPFileDownloadRequest;
import io.taptalk.Taptalk.BuildConfig;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkDownloadApiService {
    String BASE_URL = BuildConfig.BASE_URL_API;

    @POST("chat/file/download")
    Observable<ResponseBody> downloadFile(@Body TAPFileDownloadRequest request);

}

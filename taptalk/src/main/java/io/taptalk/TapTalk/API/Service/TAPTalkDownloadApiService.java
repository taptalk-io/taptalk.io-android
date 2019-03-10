package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.RequestModel.TAPFileDownloadRequest;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import rx.Observable;

public interface TAPTalkDownloadApiService {

    @Streaming
    @POST("chat/file/download")
    Observable<ResponseBody> downloadFile(@Body TAPFileDownloadRequest request,
                                          @Header("roomID") String roomID,
                                          @Header("localID") String localID);

}

package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.Taptalk.BuildConfig;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface TAPTalkMultipartApiService {
    String BASE_URL = BuildConfig.BASE_URL_API;
    //String BASE_URL = "dev.taptalk.io:8080/api/v1/";

    @Multipart
    @POST("chat/file/upload")
//    Observable<TAPBaseResponse<TAPUploadFileResponse>> uploadImage(@Part MultipartBody.Part image,
//                                                                   @Part("imageData") RequestBody imageData);
    Observable<TAPBaseResponse<TAPUploadFileResponse>> uploadImage(@Part("roomID") RequestBody roomID,
                                                                   @Part("file") RequestBody image,
                                                                   @Part("caption") RequestBody caption
            /*@Part("image") TAPUploadFileRequest request*/);
//    Observable<TAPBaseResponse<TAPUploadFileResponse>> uploadImage(@Part MultipartBody.Part image,
//                                                                   @Part("roomID") RequestBody roomID,
//                                                                   @Part("caption") RequestBody caption);

}

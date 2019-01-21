package io.taptalk.TapTalk.API.Interceptor;

import android.util.Log;

import java.io.IOException;

import io.taptalk.TapTalk.API.ResponseBody.TAPDownloadProgressResponseBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TAPDownloadRequestInterceptor implements Interceptor {
    public static final String TAG = TAPDownloadRequestInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        return chain.proceed(original).newBuilder()
                .body(new TAPDownloadProgressResponseBody(chain.proceed(original).body(), (bytesRead, contentLength, done) -> Log.e(TAG, contentLength+" : "+bytesRead ))).build();
    }
}

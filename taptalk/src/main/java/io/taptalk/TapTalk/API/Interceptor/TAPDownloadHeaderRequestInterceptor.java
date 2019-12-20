package io.taptalk.TapTalk.API.Interceptor;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import java.io.IOException;

import io.taptalk.TapTalk.API.ResponseBody.TAPDownloadProgressResponseBody;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkDownloadProgressInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.Taptalk.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.MULTIPART_CONTENT_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.NOT_USE_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.USE_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPDownloadHeaderRequestInterceptor implements Interceptor {
    public static final String TAG = TAPDownloadHeaderRequestInterceptor.class.getSimpleName();
    private int headerAuth;
    private TapTalkDownloadProgressInterface listener = new TapTalkDownloadProgressInterface() {

        @Override
        public void update(String localID, int percentage, long bytes) {
            TAPFileDownloadManager.getInstance().addDownloadProgressMap(localID, percentage, bytes);
            Intent intent = new Intent(DownloadProgressLoading);
            intent.putExtra(DownloadLocalID, localID);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        @Override
        public void finish(String localID, long bytes) {
            TAPFileDownloadManager.getInstance().addDownloadProgressMap(localID, 100, bytes);
            Intent intent = new Intent(DownloadProgressLoading);
            intent.putExtra(DownloadLocalID, localID);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }
    };


    public TAPDownloadHeaderRequestInterceptor(int headerAuth) {
        this.headerAuth = headerAuth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String APP_KEY_ID = TAPDataManager.getInstance().getApplicationID();
        String APP_KEY_SECRET = TAPDataManager.getInstance().getApplicationSecret();
        String userAgent = TAPDataManager.getInstance().getUserAgent();

        String appKey = Base64.encodeToString((APP_KEY_ID + ":" + APP_KEY_SECRET).getBytes(), Base64.NO_WRAP);

        Context context = TapTalk.appContext;

        //ini di cek untuk pertama kita cek si access token dulu kalau ada brati kita pake access token (udah login),
        // kalau ga ada kita cek lagi auth ticket nya udah ada atau belom kalau ada brati kita pake auth ticket
        // kalau nggak brati bearer aja karena brati belom request auth ticket
        String authorization;
        if (TAPDataManager.getInstance().checkAccessTokenAvailable() && (NOT_USE_REFRESH_TOKEN == headerAuth || MULTIPART_CONTENT_TYPE == headerAuth)) {
            authorization = "Bearer " + TAPDataManager.getInstance().getAccessToken();
        } else if (TAPDataManager.getInstance().checkRefreshTokenAvailable() && USE_REFRESH_TOKEN == headerAuth) {
            authorization = "Bearer " + TAPDataManager.getInstance().getRefreshToken();
        } else if (TAPDataManager.getInstance().checkAuthTicketAvailable()) {
            authorization = "Bearer " + TAPDataManager.getInstance().getAuthTicket();
        } else
            authorization = "Bearer ";

        String contentType = "application/json";

        if (MULTIPART_CONTENT_TYPE == headerAuth)
            contentType = MultipartBody.FORM.toString();

        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;
        Request request = original
                .newBuilder()
                .addHeader("Content-Type", contentType)
                .addHeader("App-Key", appKey)
                .addHeader("Authorization", authorization)
                .addHeader("Device-Identifier", deviceID)
                .addHeader("Device-Model", android.os.Build.MODEL)
                .addHeader("Device-Platform", "android")
                .addHeader("Device-OS-Version", deviceOsVersion)
                .addHeader("App-Version", BuildConfig.VERSION_NAME)
                .addHeader("User-Agent", userAgent)
                .method(original.method(), original.body())
                .build();

        Response response = chain.proceed(request);

        return response.newBuilder()
                .body(new TAPDownloadProgressResponseBody(
                        response.body(),
                        request.header("localID"),
                        listener))
                .build();
    }
}

package io.taptalk.TapTalk.API.Interceptor;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import java.io.IOException;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.Taptalk.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.APP_KEY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.APP_KEY_SECRET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.MULTIPART_CONTENT_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.NOT_USE_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.USE_REFRESH_TOKEN;

public class TAPHeaderRequestInterceptor implements Interceptor {
    public static final String TAG = TAPHeaderRequestInterceptor.class.getSimpleName();
    private int headerAuth;

    public TAPHeaderRequestInterceptor(int headerAuth) {
        this.headerAuth = headerAuth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
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
                .header("Content-Type", contentType)
                .header("App-Key", appKey)
                .header("Authorization", authorization)
                .header("Device-Identifier", deviceID)
                .header("Device-Model", android.os.Build.MODEL)
                .header("Device-Platform", "android")
                .header("Device-OS-Version", deviceOsVersion)
                .header("App-Version", BuildConfig.VERSION_NAME)
                .header("User-Agent", "android")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}

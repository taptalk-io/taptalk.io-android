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

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.MULTIPART_CONTENT_TYPE;

public class TAPHeaderRequestInterceptor implements Interceptor {
    public static final String TAG = TAPHeaderRequestInterceptor.class.getSimpleName();
    private int headerAuth;

    public TAPHeaderRequestInterceptor(int headerAuth) {
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
        
        String contentType = "application/json";

        if (MULTIPART_CONTENT_TYPE == headerAuth) {
            contentType = MultipartBody.FORM.toString();
        }

        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;
        Request request = original
                .newBuilder()
                .addHeader("Content-Type", contentType)
                .addHeader("App-Key", appKey)
                .addHeader("Device-Identifier", deviceID)
                .addHeader("Device-Model", android.os.Build.MODEL)
                .addHeader("Device-Platform", "android")
                .addHeader("Device-OS-Version", deviceOsVersion)
                .addHeader("App-Version", BuildConfig.VERSION_NAME)
                .addHeader("User-Agent", userAgent)
                .method(original.method(), original.body())
                .build();

        if (null == original.headers().get("Authorization")) {
            request = request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + TAPDataManager.getInstance().getAccessToken())
                    .build();
        }

        return chain.proceed(request);
    }
}

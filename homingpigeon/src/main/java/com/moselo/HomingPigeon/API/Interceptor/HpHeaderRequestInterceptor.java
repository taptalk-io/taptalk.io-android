package com.moselo.HomingPigeon.API.Interceptor;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Manager.HpDataManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_SECRET;

public class HpHeaderRequestInterceptor implements Interceptor {
    public static final String TAG = HpHeaderRequestInterceptor.class.getSimpleName();
    private String authTicket = null;
    public HpHeaderRequestInterceptor() {
        authTicket = null;
    }

    public HpHeaderRequestInterceptor(String authTicket) {
        this.authTicket = authTicket;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String appKey = Base64.encodeToString((APP_KEY_ID+":"+APP_KEY_SECRET).getBytes(), Base64.NO_WRAP);

        Context context = HomingPigeon.appContext;

        //ini di cek untuk pertama kita cek si access token dulu kalau ada brati kita pake access token (udah login),
        // kalau ga ada kita cek lagi auth ticket nya udah ada atau belom kalau ada brati kita pake auth ticket
        // kalau nggak brati bearer aja karena brati belom request auth ticket
        String authorization;
        if (HpDataManager.getInstance().checkRefreshTokenAvailable(context))
            authorization = "Bearer "+ HpDataManager.getInstance().getRefreshToken(context);
        else if (HpDataManager.getInstance().checkAuthTicketAvailable(context))
            authorization = "Bearer "+ HpDataManager.getInstance().getAuthToken(context);
        else
            authorization = "Bearer ";

        String deviceID = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;
        Request request = original
                .newBuilder()
                .header("Content-Type", "application/json")
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

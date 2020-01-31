package io.taptalk.TapTalk.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.API.Interceptor.TAPDownloadHeaderRequestInterceptor;
import io.taptalk.TapTalk.API.Interceptor.TAPHeaderRequestInterceptor;
import io.taptalk.TapTalk.API.Service.TAPTalkApiService;
import io.taptalk.TapTalk.API.Service.TAPTalkDownloadApiService;
import io.taptalk.TapTalk.API.Service.TAPTalkMultipartApiService;
import io.taptalk.TapTalk.API.Service.TAPTalkRefreshTokenService;
import io.taptalk.TapTalk.API.Service.TAPTalkSocketService;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.MULTIPART_CONTENT_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.NOT_USE_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.USE_REFRESH_TOKEN;

public class TAPApiConnection {

    private static final String TAG = TAPApiConnection.class.getSimpleName();
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_DISK_CACHE_SIZE = 100 * MB;
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    private static final int MAX_CACHE_STALE_TIME = 60 * 60 * 24;
    private static final int MAX_CACHE_AGE = 60 * 10;

    private TAPTalkApiService homingPigeon;
    private TAPTalkSocketService hpSocket;
    private TAPTalkRefreshTokenService hpRefresh;

    public ObjectMapper objectMapper;

    private static TAPApiConnection instance;

    public static TAPApiConnection getInstance() {
        return instance == null ? instance = new TAPApiConnection() : instance;
    }

    private TAPApiConnection() {
        this.objectMapper = TAPUtils.createObjectMapper();
        OkHttpClient httpHpClientAccessToken = buildHttpTapClient(NOT_USE_REFRESH_TOKEN);
        OkHttpClient httpHpClientRefreshToken = buildHttpTapClient(USE_REFRESH_TOKEN);

        Retrofit homingPigeonAdapter = buildApiAdapter(httpHpClientAccessToken, TAPApiManager.getBaseUrlApi());
        Retrofit hpSocketAdapter = buildApiAdapter(httpHpClientAccessToken, TAPApiManager.getBaseUrlSocket());
        Retrofit hpRefreshAdapter = buildApiAdapter(httpHpClientRefreshToken, TAPApiManager.getBaseUrlApi());

        this.homingPigeon = homingPigeonAdapter.create(TAPTalkApiService.class);
        this.hpSocket = hpSocketAdapter.create(TAPTalkSocketService.class);
        this.hpRefresh = hpRefreshAdapter.create(TAPTalkRefreshTokenService.class);
    }

    public TAPTalkApiService getHomingPigeon() {
        return homingPigeon;
    }

    public TAPTalkSocketService getHpValidate() {
        return hpSocket;
    }

    public TAPTalkRefreshTokenService getHpRefresh() {
        return hpRefresh;
    }

    public TAPTalkMultipartApiService getTapMultipart(long timeOutDuration) {
        OkHttpClient httpHpClientMultipartToken = buildHttpTapUploadClient(MULTIPART_CONTENT_TYPE, timeOutDuration);
        Retrofit tapMultipartAdapter = buildApiAdapter(httpHpClientMultipartToken, TAPApiManager.getBaseUrlApi());
        return tapMultipartAdapter.create(TAPTalkMultipartApiService.class);
    }

    public TAPTalkDownloadApiService getTapDownload(long timeOutDuration) {
        OkHttpClient httpHpClientDownload = buildHttpTapDownloadClient(NOT_USE_REFRESH_TOKEN, timeOutDuration);
        Retrofit tapDownloadAdapter = buildApiAdapter(httpHpClientDownload, TAPApiManager.getBaseUrlApi());
        return tapDownloadAdapter.create(TAPTalkDownloadApiService.class);
    }

    private OkHttpClient buildHttpTapClient(int headerAuth) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TAPHeaderRequestInterceptor(headerAuth))
                .build();
    }

    private OkHttpClient buildHttpTapUploadClient(int headerAuth, long timeOutDuration) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .readTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TAPHeaderRequestInterceptor(headerAuth))
                .build();
    }

    private OkHttpClient buildHttpTapDownloadClient(int headerAuth, long timeOutDuration) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .readTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new TAPDownloadHeaderRequestInterceptor(headerAuth))
                .build();
    }

    private Retrofit buildApiAdapter(OkHttpClient httpClient, String baseURL) {
        Executor executor = Executors.newCachedThreadPool();

        return new Retrofit.Builder()
                .callbackExecutor(executor)
                .baseUrl(baseURL)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}

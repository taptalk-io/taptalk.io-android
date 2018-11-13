package com.moselo.HomingPigeon.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.API.Interceptor.TAPHeaderRequestInterceptor;
import com.moselo.HomingPigeon.API.Service.TAPTalkApiService;
import com.moselo.HomingPigeon.API.Service.TAPTalkRefreshTokenService;
import com.moselo.HomingPigeon.API.Service.TAPTalkSocketService;
import com.moselo.HomingPigeon.BuildConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.TokenHeaderConst.NOT_USE_REFRESH_TOKEN;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.TokenHeaderConst.USE_REFRESH_TOKEN;

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
        this.objectMapper = createObjectMapper();
        OkHttpClient httpHpClientAccessToken = buildHttpHpClient(NOT_USE_REFRESH_TOKEN);
        OkHttpClient httpHpClientRefreshToken = buildHttpHpClient(USE_REFRESH_TOKEN);

        Retrofit homingPigeonAdapter = buildApiAdapter(httpHpClientAccessToken, TAPTalkApiService.BASE_URL);
        Retrofit hpSocketAdapter = buildApiAdapter(httpHpClientAccessToken, TAPTalkSocketService.BASE_URL);
        Retrofit hpRefreshAdapter = buildApiAdapter(httpHpClientRefreshToken, TAPTalkRefreshTokenService.BASE_URL);

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

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        return objectMapper;
    }

    private OkHttpClient buildHttpHpClient(int headerAuth) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TAPHeaderRequestInterceptor(headerAuth))
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

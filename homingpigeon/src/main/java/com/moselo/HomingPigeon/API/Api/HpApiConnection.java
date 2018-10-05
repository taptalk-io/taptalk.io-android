package com.moselo.HomingPigeon.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.API.Interceptor.HpHeaderRequestInterceptor;
import com.moselo.HomingPigeon.API.Service.HomingPigeonApiService;
import com.moselo.HomingPigeon.API.Service.HomingPigeonApiSocketService;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Interface.HomingPigeonSocketInterface;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HpApiConnection {

    private static final String TAG = HpApiConnection.class.getSimpleName();
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_DISK_CACHE_SIZE = 100 * MB;
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    private static final int MAX_CACHE_STALE_TIME = 60 * 60 * 24;
    private static final int MAX_CACHE_AGE = 60 * 10;

    private HomingPigeonApiService homingPigeon;
    private HomingPigeonApiSocketService hpSocket;

    public ObjectMapper objectMapper;

    private static HpApiConnection instance;

    public static HpApiConnection getInstance() {
        return instance == null ? instance = new HpApiConnection() : instance;
    }

    private HpApiConnection() {
        this.objectMapper = createObjectMapper();
        OkHttpClient httpHpClient = buildHttpHpClient();

        Retrofit homingPigeonAdapter = buildApiAdapter(httpHpClient, HomingPigeonApiService.BASE_URL);
        Retrofit hpSocketAdapter = buildApiAdapter(httpHpClient, HomingPigeonApiSocketService.BASE_URL);

        this.homingPigeon = homingPigeonAdapter.create(HomingPigeonApiService.class);
        this.hpSocket = hpSocketAdapter.create(HomingPigeonApiSocketService.class);
    }

    public HomingPigeonApiService getHomingPigeon() {
        return homingPigeon;
    }

    public HomingPigeonApiSocketService getHpSocket() {
        return hpSocket;
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

    private OkHttpClient buildHttpHpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new HpHeaderRequestInterceptor())
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

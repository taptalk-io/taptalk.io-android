package io.taptalk.TapTalk.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
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
import kotlin.Pair;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.MULTIPART_CONTENT_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.NOT_USE_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TokenHeaderConst.USE_REFRESH_TOKEN;

import android.os.Build;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class TAPApiConnection {

    private static final String TAG = TAPApiConnection.class.getSimpleName();
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_DISK_CACHE_SIZE = 100 * MB;
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    private static final int MAX_CACHE_STALE_TIME = 60 * 60 * 24;
    private static final int MAX_CACHE_AGE = 60 * 10;

    private static HashMap<String, TAPApiConnection> instances;

    private String instanceKey = "";
    private TAPTalkApiService homingPigeon;
    private TAPTalkSocketService hpSocket;
    private TAPTalkRefreshTokenService hpRefresh;

    public ObjectMapper objectMapper;

    public static TAPApiConnection getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPApiConnection instance = new TAPApiConnection(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPApiConnection> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    private TAPApiConnection(String instanceKey) {
        this.instanceKey = instanceKey;
        this.objectMapper = TAPUtils.createObjectMapper();

        OkHttpClient httpHpClientAccessToken = buildHttpTapClient(NOT_USE_REFRESH_TOKEN);
        OkHttpClient httpHpClientRefreshToken = buildHttpTapClient(USE_REFRESH_TOKEN);

        String apiBaseUrl = TAPApiManager.getApiBaseUrl(instanceKey);
        Retrofit homingPigeonAdapter = buildApiAdapter(httpHpClientAccessToken, apiBaseUrl);
        Retrofit hpRefreshAdapter = buildApiAdapter(httpHpClientRefreshToken, apiBaseUrl);
        this.homingPigeon = homingPigeonAdapter.create(TAPTalkApiService.class);
        this.hpRefresh = hpRefreshAdapter.create(TAPTalkRefreshTokenService.class);

        String socketBaseUrl = TAPApiManager.getSocketBaseUrl(instanceKey);
        Retrofit hpSocketAdapter = buildApiAdapter(httpHpClientAccessToken, socketBaseUrl);
        this.hpSocket = hpSocketAdapter.create(TAPTalkSocketService.class);
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
        Retrofit tapMultipartAdapter = buildApiAdapter(httpHpClientMultipartToken, TAPApiManager.getApiBaseUrl(instanceKey));
        return tapMultipartAdapter.create(TAPTalkMultipartApiService.class);
    }

    public TAPTalkDownloadApiService getTapDownload(long timeOutDuration) {
        OkHttpClient httpHpClientDownload = buildHttpTapDownloadClient(NOT_USE_REFRESH_TOKEN, timeOutDuration);
        Retrofit tapDownloadAdapter = buildApiAdapter(httpHpClientDownload, TAPApiManager.getApiBaseUrl(instanceKey));
        return tapDownloadAdapter.create(TAPTalkDownloadApiService.class);
    }

    private OkHttpClient buildHttpTapClient(int headerAuth) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.connectTimeout(1, TimeUnit.MINUTES);
        builder.readTimeout(1, TimeUnit.MINUTES);
        builder.writeTimeout(1, TimeUnit.MINUTES);
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new TAPHeaderRequestInterceptor(instanceKey, headerAuth));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            TapTalkTrustFactory.Companion trustFactory = TapTalkTrustFactory.Companion;
            if (trustFactory != null) {
                Pair<SSLSocketFactory, X509TrustManager> factoryManager = trustFactory.getTrustFactoryManager(TapTalk.appContext);
                if (factoryManager != null && factoryManager.getFirst() != null && factoryManager.getSecond() != null) {
                    builder.sslSocketFactory(factoryManager.getFirst(), factoryManager.getSecond());
                }
            }
        }
        return builder.build();
    }

    private OkHttpClient buildHttpTapUploadClient(int headerAuth, long timeOutDuration) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.readTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new TAPHeaderRequestInterceptor(instanceKey, headerAuth));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            TapTalkTrustFactory.Companion trustFactory = TapTalkTrustFactory.Companion;
            if (trustFactory != null) {
                Pair<SSLSocketFactory, X509TrustManager> factoryManager = trustFactory.getTrustFactoryManager(TapTalk.appContext);
                if (factoryManager != null && factoryManager.getFirst() != null && factoryManager.getSecond() != null) {
                    builder.sslSocketFactory(factoryManager.getFirst(), factoryManager.getSecond());
                }
            }
        }
        return builder.build();
    }

    private OkHttpClient buildHttpTapDownloadClient(int headerAuth, long timeOutDuration) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.readTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeOutDuration, TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(true);
        builder.addNetworkInterceptor(new TAPDownloadHeaderRequestInterceptor(instanceKey, headerAuth));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            TapTalkTrustFactory.Companion trustFactory = TapTalkTrustFactory.Companion;
            if (trustFactory != null) {
                Pair<SSLSocketFactory, X509TrustManager> factoryManager = trustFactory.getTrustFactoryManager(TapTalk.appContext);
                if (factoryManager != null && factoryManager.getFirst() != null && factoryManager.getSecond() != null) {
                    builder.sslSocketFactory(factoryManager.getFirst(), factoryManager.getSecond());
                }
            }
        }
        return builder.build();
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

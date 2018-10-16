package com.moselo.HomingPigeon.API.Api;

import android.util.Log;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.API.Service.HomingPigeonApiService;
import com.moselo.HomingPigeon.API.Service.HomingPigeonRefreshTokenService;
import com.moselo.HomingPigeon.API.Service.HomingPigeonSocketService;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Exception.ApiRefreshTokenRunningException;
import com.moselo.HomingPigeon.Exception.ApiSessionExpiredException;
import com.moselo.HomingPigeon.Exception.AuthException;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.RequestModel.HpAuthTicketRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpCommonRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomAfterRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomBeforeRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.moselo.HomingPigeon.Helper.HomingPigeon.appContext;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HttpErrorCode.TOKEN_EXPIRED;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HttpResponseStatusCode.RESPONSE_SUCCESS;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HttpResponseStatusCode.UNAUTHORIZED;

public class HpApiManager {
    private static final String TAG = HpApiManager.class.getSimpleName();
    private HomingPigeonApiService homingPigeon;
    private HomingPigeonSocketService hpSocket;
    private HomingPigeonRefreshTokenService hpRefresh;
    private static HpApiManager instance;
    private boolean isShouldRefreshToken = false;

    public static HpApiManager getInstance() {
        return instance == null ? instance = new HpApiManager() : instance;
    }

    private HpApiManager() {
        HpApiConnection connection = HpApiConnection.getInstance();
        this.homingPigeon = connection.getHomingPigeon();
        this.hpSocket = connection.getHpValidate();
        this.hpRefresh = connection.getHpRefresh();
    }

    public boolean isShouldRefreshToken() {
        return isShouldRefreshToken;
    }

    public void setUnauthorized(boolean unauthorized) {
        isShouldRefreshToken = unauthorized;
    }

    private Observable.Transformer ioToMainThreadSchedulerTransformer
            = createIOMainThreadScheduler();

    private <T> Observable.Transformer<T, T> createIOMainThreadScheduler() {
        return tObservable -> tObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applyIOMainThreadSchedulers() {
        return ioToMainThreadSchedulerTransformer;
    }

    @SuppressWarnings("unchecked")
    private <T> void execute(Observable<? extends T> o, Subscriber<T> s) {
        o.compose((Observable.Transformer<T, T>) applyIOMainThreadSchedulers())
                .flatMap((Func1<T, Observable<T>>) this::validateResponse)
                .retryWhen(o1 -> o1.flatMap((Func1<Throwable, Observable<?>>) this::validateException))
                .subscribe(s);
        isShouldRefreshToken = false;
    }

    private <T> Observable validateResponse(T t) {
        BaseResponse br = (BaseResponse) t;

        int code = br.getStatus();
        if (BuildConfig.DEBUG)
            Log.e(TAG, "validateResponse: XX HAS ERROR XX: __error_code:" + code);

        if (code == RESPONSE_SUCCESS && BuildConfig.DEBUG)
            Log.e(TAG, "validateResponse: √√ NO ERROR √√");
        else if (code == UNAUTHORIZED && isShouldRefreshToken) {
            return raiseApiRefreshTokenRunningException();
        } else if (code == UNAUTHORIZED) {
            isShouldRefreshToken = true;
            return raiseApiSessionExpiredException(br);
        }
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
        t.printStackTrace();
        return (t instanceof ApiSessionExpiredException && isShouldRefreshToken) ? refreshToken() :
                (t instanceof ApiRefreshTokenRunningException) ?
                       Observable.just(Boolean.TRUE) : Observable.error(t);
    }

    private Observable<Throwable> raiseApiSessionExpiredException(BaseResponse br) {
        return Observable.error(new ApiSessionExpiredException(br.getError().getMessage()));
    }

    private Observable<Throwable> raiseApiRefreshTokenRunningException() {
        return Observable.error(new ApiRefreshTokenRunningException());
    }

    private void updateSession(BaseResponse<HpGetAccessTokenResponse> r) {
        HpDataManager.getInstance().saveAccessToken(r.getData().getAccessToken());
        HpDataManager.getInstance().saveAccessTokenExpiry(r.getData().getAccessTokenExpiry());
        HpDataManager.getInstance().saveRefreshToken(r.getData().getRefreshToken());
        HpDataManager.getInstance().saveRefreshTokenExpiry(r.getData().getRefreshTokenExpiry());

        HpDataManager.getInstance().saveActiveUser(r.getData().getUser());
    }

    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, Subscriber<BaseResponse<HpAuthTicketResponse>> subscriber) {
        HpAuthTicketRequest request = HpAuthTicketRequest.toBuilder(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username);
        execute(homingPigeon.getAuthTicket(request), subscriber);
    }

    public void getAccessToken(Subscriber<BaseResponse<HpGetAccessTokenResponse>> subscriber) {
        execute(homingPigeon.getAccessToken(), subscriber);
    }

    public Observable<BaseResponse<HpGetAccessTokenResponse>> refreshToken() {
        return hpRefresh.refreshAccessToken()
                .compose(this.applyIOMainThreadSchedulers())
                .doOnNext(response -> {
                    if (RESPONSE_SUCCESS == response.getStatus())
                        updateSession(response);
                    if (UNAUTHORIZED == response.getStatus()){
                        HomingPigeon.refreshTokenExpired();
                    } else Observable.error(new AuthException(response.getError().getMessage()));
                }).doOnError(throwable -> {

                });
    }

    public void refreshAccessToken(Subscriber<BaseResponse<HpGetAccessTokenResponse>> subscriber) {
        execute(hpRefresh.refreshAccessToken(), subscriber);
    }

    public void validateAccessToken(Subscriber<BaseResponse<HpErrorModel>> subscriber) {
        execute(hpSocket.validateAccessToken(), subscriber);
    }

    public void getRoomList(String userID, Subscriber<BaseResponse<HpGetRoomListResponse>> subscriber) {
        HpCommonRequest request = HpCommonRequest.builderWithUserID(userID);
        execute(homingPigeon.getRoomList(request), subscriber);
    }

    public void getMessageListByRoomAfter(String roomID, Long minCreated, Long lastUpdated, Subscriber<BaseResponse<HpGetMessageListbyRoomResponse>> subscriber) {
        HpGetMessageListbyRoomAfterRequest request = new HpGetMessageListbyRoomAfterRequest(roomID, minCreated, lastUpdated);
        execute(homingPigeon.getMessageListByRoomAfter(request), subscriber);
    }

    public void getMessageListByRoomBefore(String roomID, Long maxCreated, Subscriber<BaseResponse<HpGetMessageListbyRoomResponse>> subscriber) {
        HpGetMessageListbyRoomBeforeRequest request = new HpGetMessageListbyRoomBeforeRequest(roomID, maxCreated);
        execute(homingPigeon.getMessageListByRoomBefore(request), subscriber);
    }
}

package com.moselo.HomingPigeon.API.Api;

import android.util.Log;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.API.Service.HomingPigeonApiService;
import com.moselo.HomingPigeon.API.Service.HomingPigeonSocketService;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Exception.ApiSessionExpiredException;
import com.moselo.HomingPigeon.Exception.AuthException;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.RequestModel.HpCommonRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomAfterRequest;
import com.moselo.HomingPigeon.Model.RequestModel.HpGetMessageListbyRoomBeforeRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.RequestModel.HpAuthTicketRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.moselo.HomingPigeon.Helper.HomingPigeon.appContext;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HttpResponseStatusCode.RESPONSE_SUCCESS;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HttpResponseStatusCode.UNAUTHORIZED;

public class HpApiManager {
    private static final String TAG = HpApiManager.class.getSimpleName();
    private HomingPigeonApiService homingPigeon;
    private HomingPigeonSocketService hpSocket;
    private static HpApiManager instance;
    private boolean isUnauthorized = false;
    private boolean isRefreshTokenBeenCalled = false;

    public static HpApiManager getInstance() {
        return instance == null ? instance = new HpApiManager() : instance;
    }

    private HpApiManager() {
        HpApiConnection connection = HpApiConnection.getInstance();
        this.homingPigeon = connection.getHomingPigeon();
        this.hpSocket = connection.getHpValidate();
    }

    public boolean isUnauthorized() {
        return isUnauthorized;
    }

    public void setUnauthorized(boolean unauthorized) {
        isUnauthorized = unauthorized;
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
    }

    private <T> Observable validateResponse(T t) {
        BaseResponse br = (BaseResponse) t;

//        if (br.getRefreshToken() != null && br.getRefreshToken() != null)
//            updateSession((BaseResponse) t);

        //if (br.getError() != null) {
        int code = br.getStatus();
        if (BuildConfig.DEBUG)
            Log.e(TAG, "validateResponse: XX HAS ERROR XX: __error_code:" + code);

//            if (code == ERR_FORBIDDEN)
//                return raiseApiTokenException(br);
        if (code == RESPONSE_SUCCESS && BuildConfig.DEBUG)
            Log.e(TAG, "validateResponse: √√ NO ERROR √√");
        else if (code == UNAUTHORIZED)
            return raiseApiSessionExpiredException(br);
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
        t.printStackTrace();
//        return (t instanceof ApiTokenException)
//                ? createApiToken() : t instanceof ApiSessionExpiredException
//                ? refreshToken() : Observable.error(t);
//        return (t instanceof ApiSessionExpiredException) ? refreshToken() : Observable.error(t);
        if (t instanceof ApiSessionExpiredException && !isRefreshTokenBeenCalled) {
            isRefreshTokenBeenCalled = true;
            return refreshToken();
        } else return Observable.error(t);
    }

    private Observable<Throwable> raiseApiSessionExpiredException(BaseResponse br) {
        return Observable.error(new ApiSessionExpiredException(br.getError().getMessage()));
    }

    private void updateSession(BaseResponse<HpGetAccessTokenResponse> r) {
        HpDataManager.getInstance().saveRefreshToken(appContext, r.getData().getRefreshToken());
        HpDataManager.getInstance().saveRefreshTokenExpiry(appContext, r.getData().getRefreshTokenExpiry());
        HpDataManager.getInstance().saveAccessToken(appContext, r.getData().getAccessToken());
        HpDataManager.getInstance().saveAccessTokenExpiry(appContext, r.getData().getAccessTokenExpiry());

        HpDataManager.getInstance().saveActiveUser(appContext, r.getData().getUser());
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
        isUnauthorized = true;
        return homingPigeon.refreshAccessToken()
                .compose(this.applyIOMainThreadSchedulers())
                .doOnNext(response -> {
                    Log.e(TAG, "refreshToken: ");
                    isUnauthorized = false;
                    isRefreshTokenBeenCalled = false;
                    if (RESPONSE_SUCCESS == response.getStatus())
                        updateSession(response);
                    else Observable.error(new AuthException(response.getError().getMessage()));
                }).doOnError(throwable -> {

                });
    }

    public void refreshAccessToken(Subscriber<BaseResponse<HpGetAccessTokenResponse>> subscriber) {
        execute(homingPigeon.refreshAccessToken(), subscriber);
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

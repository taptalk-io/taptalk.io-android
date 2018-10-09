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
import com.moselo.HomingPigeon.Model.ErrorModel;
import com.moselo.HomingPigeon.Model.RequestModel.CommonRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.RequestModel.AuthTicketRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.GetRoomListResponse;

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
                .flatMap((Func1<T, Observable<T>>) t -> validateResponse(t))
                .retryWhen(o1 -> o1.flatMap((Func1<Throwable, Observable<?>>) t -> validateException(t)))
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
        //}
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
        t.printStackTrace();
//        return (t instanceof ApiTokenException)
//                ? createApiToken() : t instanceof ApiSessionExpiredException
//                ? refreshToken() : Observable.error(t);
        return (t instanceof ApiSessionExpiredException) ? refreshToken() : Observable.error(t);
    }

    private Observable<Throwable> raiseApiSessionExpiredException(BaseResponse br) {
        return Observable.error(new ApiSessionExpiredException(br.getError().getMessage()));
    }

    private void updateSession(BaseResponse<GetAccessTokenResponse> r) {
        Log.e(TAG, "updateSession: " + HpUtils.getInstance().toJsonString(r.getData()));
        HpDataManager.getInstance().saveRefreshToken(appContext, r.getData().getRefreshToken());
        HpDataManager.getInstance().saveRefreshTokenExpiry(appContext, r.getData().getRefreshTokenExpiry());
        HpDataManager.getInstance().saveAccessToken(appContext, r.getData().getAccessToken());
        HpDataManager.getInstance().saveAccessTokenExpiry(appContext, r.getData().getAccessTokenExpiry());

        HpDataManager.getInstance().saveActiveUser(appContext, r.getData().getUser());
    }

    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, Subscriber<BaseResponse<AuthTicketResponse>> subscriber) {
        AuthTicketRequest request = AuthTicketRequest.toBuilder(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username);
        execute(homingPigeon.getAuthTicket(request), subscriber);
    }

    public void getAccessToken(Subscriber<BaseResponse<GetAccessTokenResponse>> subscriber) {
        execute(homingPigeon.getAccessToken(), subscriber);
    }

    public Observable<BaseResponse<GetAccessTokenResponse>> refreshToken() {
        isUnauthorized = true;
        return homingPigeon.refreshAccessToken()
                .compose(this.applyIOMainThreadSchedulers())
                .doOnNext(response -> {
                    Log.e(TAG, "refreshToken: ");
                    isUnauthorized = false;
                    if (RESPONSE_SUCCESS == response.getStatus())
                        updateSession(response);
                    else Observable.error(new AuthException(response.getError().getMessage()));
                }).doOnError(throwable -> {

                });
    }

    public void refreshAccessToken(Subscriber<BaseResponse<GetAccessTokenResponse>> subscriber) {
        execute(homingPigeon.refreshAccessToken(), subscriber);
    }

    public void validateAccessToken(Subscriber<BaseResponse<ErrorModel>> subscriber) {
        execute(hpSocket.validateAccessToken(), subscriber);
    }

    public void getRoomList(String userID, Subscriber<BaseResponse<GetRoomListResponse>> subscriber) {
        CommonRequest request = CommonRequest.builderWithUserID(userID);
        execute(homingPigeon.getRoomList(request), subscriber);
    }
}

package com.moselo.SampleApps.Api;

import android.util.Log;

import com.moselo.SampleApps.Api.ApiConnection;
import com.moselo.SampleApps.Api.HomingPigeonApiService;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ApiManager {
    private static final String TAG = ApiManager.class.getSimpleName();
    private HomingPigeonApiService homingPigeon;
    private static ApiManager instance;

    public static ApiManager getInstance() {
        return instance == null ? instance = new ApiManager() : instance;
    }

    private ApiManager() {
        ApiConnection connection = ApiConnection.getInstance();
        this.homingPigeon = connection.getHomingPigeon();
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
//        BaseResponse br = (BaseResponse) t;
//
//        if (br.getAccessToken() != null && br.getRefreshToken() != null)
//            updateSession((BaseResponse) t);
//
//        if (br.getError() != null) {
//            int code = br.getError().getCode();
//            if (BuildConfig.DEBUG)
//                LogUtils.logError(LOG_TAG, "validateResponse: XX HAS ERROR XX: __error_code:" + code);
//
//            if (code == ERR_FORBIDDEN)
//                return raiseApiTokenException(br);
//            else if (code == ERR_TOKEN_EXPIRED /*|| code == ERR_UNAUTHORIZED*/ || code == OK_ZOMBIE)
//                return raiseApiSessionExpiredException(br);
//        } else if (br.getError() == null) {
//            if (BuildConfig.DEBUG)
//                LogUtils.logError(LOG_TAG, "validateResponse: √√ NO ERROR √√");
//        }
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
        t.printStackTrace();
//        return (t instanceof ApiTokenException)
//                ? createApiToken() : t instanceof ApiSessionExpiredException
//                ? refreshToken() : Observable.error(t);
        return Observable.error(t);
    }

}

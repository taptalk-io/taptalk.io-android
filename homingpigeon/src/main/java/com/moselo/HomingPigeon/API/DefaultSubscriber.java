package com.moselo.HomingPigeon.API;

import android.util.Log;

import com.moselo.HomingPigeon.API.View.DefaultDataView;
import com.moselo.HomingPigeon.API.View.IView;

import rx.Subscriber;

/**
 * MoseloUser by Fadhlan on 3/3/17.
 */

public class DefaultSubscriber<T extends BaseResponse<D>, V extends DefaultDataView<D> &
        IView<D>, D>
        extends Subscriber<T> {
    private static final String LOG_TAG = DefaultSubscriber.class.getSimpleName();

    protected V view;

    public DefaultSubscriber() {
        super();
    }

    public DefaultSubscriber(V view) {
        this.view = view;
        if (view == null) throw new IllegalArgumentException("ERR: null view");
    }

    @Override
    public void onStart() {
        super.onStart();
        view.startLoading();
    }

    @Override
    public void onCompleted() {
        view.endLoading();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.e(LOG_TAG, "onError: ", e);
        view.onError(e.getMessage());
        view.onError(e);
    }

    @Override
    public void onNext(T t) {
        if (t.getError() != null && 200 != t.getError().getStatus()) {
            Log.e(LOG_TAG, "onNext: "+t.getError().getStatus() );
            view.onError(t.getError());
        } else view.onSuccess(t.getData());
    }
}

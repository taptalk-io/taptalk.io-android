package io.taptalk.TapTalk.API.Subscriber;

import android.util.Log;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.API.View.TapView;
import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import rx.Subscriber;

/**
 * MoseloUser by Fadhlan on 3/3/17.
 */

public class TAPDefaultSubscriber<T extends TAPBaseResponse<D>, V extends TapDefaultDataView<D> &
        TapView<D>, D>
        extends Subscriber<T> {
    private static final String LOG_TAG = TAPDefaultSubscriber.class.getSimpleName();

    protected V view;
    protected String localID;

    public TAPDefaultSubscriber() {
        super();
    }

    public TAPDefaultSubscriber(V view) {
        this.view = view;
        if (view == null) throw new IllegalArgumentException("ERR: null view");
    }

    public TAPDefaultSubscriber(V view, String localID) {
        this.view = view;
        this.localID = localID;
        if (view == null) throw new IllegalArgumentException("ERR: null view");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != localID) view.startLoading(localID);
        else view.startLoading();
    }

    @Override
    public void onCompleted() {
        if (null != localID) view.endLoading(localID);
        else view.endLoading();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.e("><><><", "onError: ",e );
        if (null != localID) {
            view.onError(e.getMessage(), localID);
            view.onError(e, localID);
        } else {
            view.onError(e.getMessage());
            view.onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        if (t.getError() != null && 200 != t.getStatus() && null != localID) {
            view.onError(t.getError(), localID);
        } else if (t.getError() != null && 200 != t.getStatus()) {
            view.onError(t.getError());
        } else if (null != localID) {
            view.onSuccess(t.getData(), localID);
        } else {
            view.onSuccess(t.getData());
        }
    }
}

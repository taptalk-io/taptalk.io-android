package io.taptalk.TapTalk.API.Subscriber;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class TAPBaseSubscriber<V extends TapDefaultDataView<ResponseBody>> extends Subscriber<ResponseBody> {

    protected V view;

    public TAPBaseSubscriber(V view) {
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
        view.onError(e.getMessage());
        view.onError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (null == responseBody) {
            view.onError(new TAPErrorModel("999", "Unknown Error", ""));
        } else {
            view.onSuccess(responseBody);
        }
    }
}

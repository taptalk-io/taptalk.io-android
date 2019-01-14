package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TAPErrorModel;

/**
 * Created by Fadhlan on 6/15/17.
 */

public abstract class TapDefaultDataView<T> implements TapView<T> {
    @Override public void startLoading() {}
    @Override public void startLoading(String localID) {}
    @Override public void endLoading() {}
    @Override public void endLoading(String localID) {}
    @Override public void onEmpty(String message) {}
    @Override public void onSuccess(T response) {}
    @Override public void onSuccess(T t, String localID) {}
    @Override public void onSuccessMessage(String message) {}
    @Override public void onError(TAPErrorModel error) {}
    @Override public void onError(TAPErrorModel error, String localID) {}
    @Override public void onError(String errorMessage) {}
    @Override public void onError(String errorMessage, String localID) {}
    @Override public void onError(Throwable throwable) {}
    @Override public void onError(Throwable throwable, String localID) {}
}


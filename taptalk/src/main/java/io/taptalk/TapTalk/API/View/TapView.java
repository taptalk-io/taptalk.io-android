package io.taptalk.TapTalk.API.View;

import io.taptalk.TapTalk.Model.TAPErrorModel;

/**
 * Created by Fadhlan on 4/19/17.
 */

public interface TapView<T> {
    void startLoading();

    void endLoading();

    void onEmpty(String message);

    void onSuccess(T t);

    void onSuccessMessage(String message);

    void onError(TAPErrorModel error);

    void onError(String errorMessage);

    void onError(Throwable throwable);
}

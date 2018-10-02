package com.moselo.HomingPigeon.API.View;

import com.moselo.HomingPigeon.Model.ErrorModel;

/**
 * Created by Fadhlan on 4/19/17.
 */

public interface HpView<T> {
    void startLoading();

    void endLoading();

    void onEmpty(String message);

    void onSuccess(T t);

    void onSucessMessage(String message);

    void onError(ErrorModel error);

    void onError(String errorMessage);

    void onError(Throwable throwable);
}

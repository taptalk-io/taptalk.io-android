package com.moselo.HomingPigeon.API.View;

import com.moselo.HomingPigeon.Model.HpErrorModel;

/**
 * Created by Fadhlan on 4/19/17.
 */

public interface HpView<T> {
    void startLoading();

    void endLoading();

    void onEmpty(String message);

    void onSuccess(T t);

    void onSuccessMessage(String message);

    void onError(HpErrorModel error);

    void onError(String errorMessage);

    void onError(Throwable throwable);
}

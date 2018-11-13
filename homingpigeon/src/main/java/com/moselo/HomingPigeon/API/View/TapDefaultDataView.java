package com.moselo.HomingPigeon.API.View;

import com.moselo.HomingPigeon.Model.TAPErrorModel;

/**
 * Created by Fadhlan on 6/15/17.
 */

public abstract class TapDefaultDataView<T> implements TapView<T> {
    @Override
    public void startLoading() {

    }

    @Override
    public void endLoading() {

    }

    @Override
    public void onEmpty(String message) {

    }

    @Override
    public void onSuccess(T response) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void onError(TAPErrorModel error) {

    }

    @Override
    public void onError(String errorMessage) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}

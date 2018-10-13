package com.moselo.HomingPigeon.Exception;

/**
 * Created by wkndev on 01/05/17.
 */

public class ApiRefreshTokenRunningException extends Exception {

    private static final String LOG_TAG = ApiRefreshTokenRunningException.class.getSimpleName();


    public ApiRefreshTokenRunningException() {
        super("REFRESH TOKEN IS RUNNING");
    }
}

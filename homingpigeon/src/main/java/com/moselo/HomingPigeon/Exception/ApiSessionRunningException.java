package com.moselo.HomingPigeon.Exception;

/**
 * Created by wkndev on 01/05/17.
 */

public class ApiSessionRunningException extends Exception {

    private static final String LOG_TAG = ApiSessionRunningException.class.getSimpleName();


    public ApiSessionRunningException(String message) {
        super(message);
    }
}

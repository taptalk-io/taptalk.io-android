package com.moselo.HomingPigeon.Exception;

/**
 * Created by wkndev on 01/05/17.
 */

public class ApiSessionExpiredException extends Exception {

    private static final String LOG_TAG = ApiSessionExpiredException.class.getSimpleName();


    public ApiSessionExpiredException(String message) {
        super(message);
    }
}

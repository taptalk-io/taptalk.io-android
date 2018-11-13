package com.moselo.HomingPigeon.Exception;

/**
 * Created by wkndev on 01/05/17.
 */

public class TAPApiSessionExpiredException extends Exception {

    private static final String LOG_TAG = TAPApiSessionExpiredException.class.getSimpleName();


    public TAPApiSessionExpiredException(String message) {
        super(message);
    }
}

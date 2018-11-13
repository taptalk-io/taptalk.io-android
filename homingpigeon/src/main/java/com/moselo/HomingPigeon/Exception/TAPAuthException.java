package com.moselo.HomingPigeon.Exception;

/**
 * Created by Fadhlan on 5/9/17.
 */

public class TAPAuthException extends Exception {

    private static final String LOG_TAG = TAPAuthException.class.getSimpleName();

    public TAPAuthException(Throwable cause) {
        super(cause);
    }

    public TAPAuthException(String message) {
        super(message);
    }
}

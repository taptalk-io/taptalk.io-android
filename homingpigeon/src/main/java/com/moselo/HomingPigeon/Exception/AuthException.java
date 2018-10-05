package com.moselo.HomingPigeon.Exception;

/**
 * Created by Fadhlan on 5/9/17.
 */

public class AuthException extends Exception {

    private static final String LOG_TAG = AuthException.class.getSimpleName();

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message) {
        super(message);
    }
}

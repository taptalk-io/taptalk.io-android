package io.taptalk.TapTalk.Exception;

/**
 * Created by wkndev on 01/05/17.
 */

public class TAPApiRefreshTokenRunningException extends Exception {

    private static final String LOG_TAG = TAPApiRefreshTokenRunningException.class.getSimpleName();


    public TAPApiRefreshTokenRunningException() {
        super("REFRESH TOKEN IS RUNNING");
    }
}

package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreSendMessageListener implements TapSendMessageInterface {

    /**
     * onStart
     * @param message
     * Message model has been successfully generated
     */
    @Override
    public void onStart(TAPMessageModel message) {

    }

    /**
     * onSuccess
     * @param message
     * Message has been sent to the server
     */
    @Override
    public void onSuccess(TAPMessageModel message) {

    }

    /**
     * onError
     * @param message
     * Message failed to send
     */
    @Override
    public void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage) {

    }


    /**
     * onProgress
     * @param message
     * Media file upload is in progress
     */
    @Override
    public void onProgress(TAPMessageModel message, int percentage, long bytes) {

    }


    /**
     * onTemporaryMessageCreated
     * @param message
     * Temporary message with data is created for sending media messages with remote url
     */
    @Override
    public void onTemporaryMessageCreated(TAPMessageModel message) {

    }
}

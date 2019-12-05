package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreSendMessageListener implements TapSendMessageInterface {

    @Override
    public void onStart(TAPMessageModel message) {

    }

    @Override
    public void onSuccess(TAPMessageModel message) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }

    @Override
    public void onProgress(TAPMessageModel message, int percentage, long bytes) {

    }
}

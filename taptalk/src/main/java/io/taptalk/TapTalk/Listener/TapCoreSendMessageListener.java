package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TapCoreSendMessageListener implements TapSendMessageInterface {

    @Override
    public void onStart(TAPMessageModel tapMessageModel) {

    }

    @Override
    public void onSuccess(TAPMessageModel tapMessageModel) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }

    @Override
    public void onProgress(TAPMessageModel tapMessageModel, int percentage, long bytes) {

    }
}

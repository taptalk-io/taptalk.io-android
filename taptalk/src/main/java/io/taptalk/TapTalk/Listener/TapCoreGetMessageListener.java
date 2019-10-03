package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapGetMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreGetMessageListener implements TapGetMessageInterface {

    @Override
    public void onSuccess(List<TAPMessageModel> messages) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

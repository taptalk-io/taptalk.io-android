package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.util.List;

import io.taptalk.TapTalk.Interface.TapGetOlderMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreGetOlderMessageListener implements TapGetOlderMessageInterface {

    @Override
    public void onSuccess(List<TAPMessageModel> messages, Boolean hasMoreData) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

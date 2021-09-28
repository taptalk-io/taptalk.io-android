package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.io.File;

import io.taptalk.TapTalk.Interface.TapFileDownloadInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreFileDownloadListener implements TapFileDownloadInterface {

    @Override
    public void onSuccess(TAPMessageModel message, File file) {

    }

    @Override
    public void onError(TAPMessageModel message, String errorCode, String errorMessage) {

    }

    @Override
    public void onProgress(TAPMessageModel message, int percentage, long bytes) {

    }
}

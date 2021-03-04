package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapFileUploadInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreFileUploadListener implements TapFileUploadInterface {
    @Override
    public void onProgress(int percentage, long bytes) {

    }

    @Override
    public void onSuccess(String fileID, String fileURL) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

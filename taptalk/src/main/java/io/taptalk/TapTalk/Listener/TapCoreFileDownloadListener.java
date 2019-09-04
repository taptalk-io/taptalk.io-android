package io.taptalk.TapTalk.Listener;

import java.io.File;

import io.taptalk.TapTalk.Interface.TapFileDownloadInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TapCoreFileDownloadListener implements TapFileDownloadInterface {

    @Override
    public void onSuccess(File file) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }

    @Override
    public void onProgress(TAPMessageModel message, int percentage, long bytes) {

    }
}

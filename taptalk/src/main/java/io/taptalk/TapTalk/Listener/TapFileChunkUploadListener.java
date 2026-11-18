package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.io.File;
import java.util.List;

import io.taptalk.TapTalk.Interface.TapFileChunkUploadInterface;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateUploadSessionResponse;

@Keep
public abstract class TapFileChunkUploadListener implements TapFileChunkUploadInterface {
    @Override
    public void onFileSplit(List<File> fileChunks) {

    }

    @Override
    public void onSessionCreated(TAPCreateUploadSessionResponse session) {

    }

    @Override
    public void onChunkUploaded(int chunkIndex, String uploadSessionID) {

    }

    @Override
    public void onSuccess(String uploadSessionID) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}

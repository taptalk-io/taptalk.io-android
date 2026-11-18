package io.taptalk.TapTalk.Interface;

import java.io.File;
import java.util.List;

import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateUploadSessionResponse;

public interface TapFileChunkUploadInterface {
    void onFileSplit(List<File> fileChunks);

    void onSessionCreated(TAPCreateUploadSessionResponse session);

    void onChunkUploaded(int chunkIndex, String uploadSessionID);

    void onSuccess(String uploadSessionID);

    void onError(String errorCode, String errorMessage);
}

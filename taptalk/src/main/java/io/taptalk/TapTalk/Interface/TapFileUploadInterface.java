package io.taptalk.TapTalk.Interface;

public interface TapFileUploadInterface {
    void onProgress(int percentage, long bytes);

    void onSuccess(String fileID, String fileURL);

    void onError(String errorCode, String errorMessage);
}

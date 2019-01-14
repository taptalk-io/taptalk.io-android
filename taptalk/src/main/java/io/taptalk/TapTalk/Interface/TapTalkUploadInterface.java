package io.taptalk.TapTalk.Interface;

public interface TapTalkUploadInterface {
    void onProgressLoading(String localID, int progress);
    void onProgressFinish(String localID);
    void onUploadFailed(String localID);
    void onUploadCanceled(String localID);
}

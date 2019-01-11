package io.taptalk.TapTalk.Interface;

public interface TapTalkUploadProgressInterface {
    void onProgressLoading(String localID, int progress);
    void onProgressFinish(String localID);
}

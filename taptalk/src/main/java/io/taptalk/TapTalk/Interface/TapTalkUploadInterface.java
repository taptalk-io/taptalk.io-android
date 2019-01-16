package io.taptalk.TapTalk.Interface;

import java.util.HashMap;

public interface TapTalkUploadInterface {
    void onProgressLoading(String localID);
    void onProgressFinish(String localID, HashMap<String, Object> imageDataModel);
    void onUploadFailed(String localID);
    void onUploadCanceled(String localID);
}

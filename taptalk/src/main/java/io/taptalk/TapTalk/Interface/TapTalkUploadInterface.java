package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPDataImageModel;

public interface TapTalkUploadInterface {
    void onProgressLoading(String localID);
    void onProgressFinish(String localID, TAPDataImageModel imageDataModel);
    void onUploadFailed(String localID);
    void onUploadCanceled(String localID);
}

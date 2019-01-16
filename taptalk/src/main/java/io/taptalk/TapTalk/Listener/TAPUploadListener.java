package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkUploadInterface;
import io.taptalk.TapTalk.Model.TAPDataImageModel;

public abstract class TAPUploadListener implements TapTalkUploadInterface {
    @Override public void onProgressLoading(String localID) {}
    @Override public void onProgressFinish(String localID, TAPDataImageModel imageDataModel) {}
    @Override public void onUploadFailed(String localID) {}
    @Override public void onUploadCanceled(String localID) {}
}

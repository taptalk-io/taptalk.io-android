package io.taptalk.TapTalk.Listener;

import java.util.HashMap;

import io.taptalk.TapTalk.Interface.TapTalkUploadInterface;

public abstract class TAPUploadListener implements TapTalkUploadInterface {
    @Override public void onProgressLoading(String localID) {}
    @Override public void onProgressFinish(String localID, HashMap<String, Object> imageDataModel) {}
    @Override public void onUploadFailed(String localID) {}
    @Override public void onUploadCanceled(String localID) {}
}

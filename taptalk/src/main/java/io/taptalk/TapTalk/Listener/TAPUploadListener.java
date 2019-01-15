package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkUploadInterface;

public abstract class TAPUploadListener implements TapTalkUploadInterface {
    @Override public void onProgressLoading(String localID) {}
    @Override public void onProgressFinish(String localID) {}
    @Override public void onUploadFailed(String localID) {}
    @Override public void onUploadCanceled(String localID) {}
}

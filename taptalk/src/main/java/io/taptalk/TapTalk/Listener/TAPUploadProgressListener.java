package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkUploadProgressInterface;

public abstract class TAPUploadProgressListener implements TapTalkUploadProgressInterface {
    @Override public void onProgressLoading(String localID, int progress) {}
    @Override public void onProgressFinish(String localID) {}
    @Override public void onUploadFailed(String localID) {}
}

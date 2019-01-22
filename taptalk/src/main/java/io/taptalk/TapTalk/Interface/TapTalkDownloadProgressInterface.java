package io.taptalk.TapTalk.Interface;

public interface TapTalkDownloadProgressInterface {
    void update(int percentage, String roomID, String localID);
    void finish(String roomID, String localID);
}

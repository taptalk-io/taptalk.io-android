package io.taptalk.TapTalk.Interface;

public interface TapTalkDownloadProgressInterface {
    void update(int percentage, String localID);
    void finish(String localID);
}

package io.taptalk.TapTalk.Interface;

public interface TapTalkDownloadProgressInterface {
    void update(String localID, int percentage, long bytes);

    void finish(String localID, long bytes);
}

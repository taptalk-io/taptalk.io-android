package io.taptalk.TapTalk.Interface;

public interface TapTalkGeneralInterface<T> {
    void onClick();
    void onClick(int position);
    void onClick(int position, T item);
}

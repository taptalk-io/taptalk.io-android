package io.taptalk.TapTalk.Interface;

import java.util.List;

public interface TapCoreGetStringListInterface {
    void onSuccess(List<String> stringList);

    void onError(String errorCode, String errorMessage);
}

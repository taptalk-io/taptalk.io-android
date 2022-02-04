package io.taptalk.TapTalk.Interface;

import java.util.List;

public interface TapCoreUpdateMessageStatusInterface {
    void onSuccess(List<String> updatedMessageIDs);

    void onError(String errorCode, String errorMessage);
}

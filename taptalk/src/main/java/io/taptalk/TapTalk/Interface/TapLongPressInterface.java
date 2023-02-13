package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;

public interface TapLongPressInterface {
    void onMessageLongPressMenuItemSelected(TapLongPressMenuItem longPressMenuItem, TAPMessageModel messageModel);
}

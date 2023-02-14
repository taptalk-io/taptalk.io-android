package io.taptalk.TapTalk.Interface;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;

public interface TapLongPressInterface {
    void onLongPressMenuItemSelected(TapLongPressMenuItem longPressMenuItem, @Nullable TAPMessageModel messageModel);
}

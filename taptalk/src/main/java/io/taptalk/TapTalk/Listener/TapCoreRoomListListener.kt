package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreRoomListInterface

@Keep
abstract class TapCoreRoomListListener : TapCoreRoomListInterface{
    override fun onChatRoomDeleted(roomID: String) {

    }
}
package io.taptalk.TapTalk.Listener

import androidx.annotation.Keep
import io.taptalk.TapTalk.Interface.TapCoreRoomListInterface

@Keep
abstract class TapCoreRoomListListener : TapCoreRoomListInterface{
    override fun onChatRoomDeleted(roomID: String) {

    }

    override fun onChatRoomMuted(roomID: String, expiryTime: Long) {

    }

    override fun onChatRoomUnmuted(roomID: String) {

    }

    override fun onChatRoomPinned(roomID: String) {

    }

    override fun onChatRoomUnpinned(roomID: String) {

    }
}
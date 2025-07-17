package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel

class TapMainRoomListViewModel : ViewModel() {

    enum class RoomListState {
        STATE_SEARCH_CHAT, STATE_ROOM_LIST
    }

    var state = RoomListState.STATE_ROOM_LIST
}
